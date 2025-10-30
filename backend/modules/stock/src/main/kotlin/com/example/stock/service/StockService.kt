package com.example.stock.service

import com.example.stock.dto.OwnerDto
import com.example.stock.dto.StockDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.dto.StockLotSellDto
import com.example.stock.model.BuyTransaction
import com.example.stock.model.SellTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.IncomingHistory
import com.example.stock.model.BenefitHistory
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.SellTransactionRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.service.BuyTransactionService
import com.example.stock.service.SellTransactionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 株式ロットのサービス。
 * 株式ロットに関するビジネスロジックを処理します。
 */
@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository,
    private val buyTransactionRepository: BuyTransactionRepository,
    private val sellTransactionRepository: SellTransactionRepository,
    private val buyTransactionService: BuyTransactionService,
    private val sellTransactionService: SellTransactionService,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository,
) {
    

    /**
     * すべての株式ロットを取得します。
     * @return StockLotのリスト
     */
    open fun findAll(): List<StockLot> {
        return stockLotRepository.findAll()
    }

    /**
     * 所有者IDによって株式ロットを検索します。
     * @param ownerId 所有者ID
     * @return StockLotのリスト
     */
    fun findByOwnerId(ownerId: Int): List<StockLot> {
        return stockLotRepository.findByOwnerId(ownerId)
    }

    /**
     * IDに基づいてStockLotを検索します。
     * @param id 検索するStockLotのID
     * @return 見つかったStockLot。存在しない場合はnull。
     */
    open fun findById(id: Int): StockLot? {
        return stockLotRepository.findById(id).orElse(null)
    }

    /**
     * 現在保有単元が0より大きいすべての株式ロットを取得し、
     * 各ロットについて購入取引履歴から平均取得単価を計算したDTOに変換して返します。
     *
     * 平均取得単価の計算:
     * - 各購入取引について price * unit を集計し、fee を加算した総コストを算出します。
     * - 総単元数で総コストを割り、小数点以下2桁で四捨五入(HALF_UP)します。
     * - 取引が無い、または総単元数が0の場合は BigDecimal.ZERO を返します。
     *
     * @return 平均取得単価を含む `StockLotResponseDto` のリスト
     */
    fun findAllWithAveragePrice(): List<StockLotResponseDto> {
        val stockLots = stockLotRepository.findAllByCurrentUnitGreaterThan(0)
        return stockLots.map { stockLot ->
            createDtoWithAveragePrice(stockLot)
        }
    }

    /**
     * 指定した所有者IDに紐づく、現在保有単元が0より大きい株式ロットを取得し、
     * 平均取得単価を計算したDTOに変換して返します。
     *
     * @param ownerId 所有者のID
     * @return 平均取得単価を含む `StockLotResponseDto` のリスト
     */
    fun findByOwnerIdWithAveragePrice(ownerId: Int): List<StockLotResponseDto> {
        val stockLots = stockLotRepository.findByOwnerIdAndCurrentUnitGreaterThan(ownerId, 0)
        return stockLots.map { stockLot ->
            createDtoWithAveragePrice(stockLot)
        }
    }

    /**
     * 指定したIDの株式ロットを取得し、該当ロットが存在する場合は平均取得単価を計算したDTOを返します。
     *
     * @param id 検索するStockLotのID
     * @return `StockLotResponseDto`（存在しない場合はnull）
     */
    fun findByIdWithAveragePrice(id: Int): StockLotResponseDto? {
        val stockLot = stockLotRepository.findById(id).orElse(null)
        return stockLot?.let { createDtoWithAveragePrice(it) }
    }

    /**
     * 指定した `StockLot` から `StockLotResponseDto` を作成します。
     * DTOにはロット情報に加えて、当該ロットに紐づく購入取引から算出した平均取得単価が含まれます。
     *
     * 平均取得単価の算出詳細:
     * - 取引ごとに (price * unit) を合算し、各取引の fee を加えて総コストを求めます。
     * - 総単元数で総コストを割り、小数点以下2桁で四捨五入(HALF_UP)します。
     * - 取引が無い、または総単元数が0の場合は BigDecimal.ZERO を平均価格とします。
     *
     * @param stockLot 平均価格を計算してDTOに変換する対象の `StockLot`
     * @return 平均価格を埋めた `StockLotResponseDto`
     */
    private fun createDtoWithAveragePrice(stockLot: StockLot): StockLotResponseDto {
        val transactions = buyTransactionRepository.findByStockLotId(stockLot.id)
        val averagePrice = if (transactions.isNotEmpty()) {
            val totalCost = transactions.fold(BigDecimal.ZERO) { acc, tx ->
                acc.add(tx.price.multiply(BigDecimal(tx.unit))).add(tx.fee)
            }
            val totalUnits = transactions.sumOf { it.unit }
            if (totalUnits > 0) {
                totalCost.divide(BigDecimal(totalUnits), 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        } else {
            BigDecimal.ZERO
        }

        val earliestTransaction = buyTransactionRepository.findFirstByStockLotOrderByTransactionDateAsc(stockLot)
        val purchaseDate = earliestTransaction?.transactionDate

        return StockLotResponseDto(
            id = stockLot.id,
            owner = OwnerDto(id = stockLot.owner.id, name = stockLot.owner.name),
            stock = StockDto(
                id = stockLot.stock.id,
                code = stockLot.stock.code,
                name = stockLot.stock.name,
                currentPrice = stockLot.stock.currentPrice,
                minimalUnit = stockLot.stock.minimalUnit,
            ),
            currentUnit = stockLot.currentUnit,
            averagePrice = averagePrice,
            purchaseDate = purchaseDate,
        )
    }


    /**
     * 単一の株式ロットを作成し、同時に購入取引を作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param unit 初期保有単元数
     * @param price 購入価格（購入取引に使用）
     * @param fee 手数料（購入取引に使用）
     * @param isNisa NISAフラグ
     * @param transactionDate 取引日（購入取引に使用）
     * @return 作成されたStockLot
     */
    // StockLotServiceで実装する場合
    fun createStockLot(
        owner: Owner,
        stock: Stock,
        unit: Int,
        price: BigDecimal,
        fee: BigDecimal,
        isNisa: Boolean,
        transactionDate: LocalDate
    ): StockLot {
        // トランザクション内で一括処理
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = unit
        )
        stockLotRepository.save(stockLot)
        
        val buyTransaction = BuyTransaction(
            stockLot = stockLot,
            unit = unit,
            price = price,
            fee = fee,
            isNisa = isNisa,
            transactionDate = transactionDate
        )
        buyTransactionRepository.save(buyTransaction)
        
        return stockLot
    }
    /**
     * 単一の株式ロットを作成し、同時に購入取引も作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param currentUnit 現在の単元数
     * @param buyTransaction 作成する購入取引（stockLotは無視される）
     * @return 作成されたStockLot
     */
    fun createStockLotAndBuyTransaction(
        owner: Owner,
        stock: Stock,
        currentUnit: Int,
        buyTransaction: BuyTransaction,
    ): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit,
        )
        val savedStockLot = stockLotRepository.save(stockLot)
        // BuyTransactionのstockLotを保存したものに差し替えて作成
        val transactionToSave = buyTransaction.copy(stockLot = savedStockLot)
        buyTransactionService.create(transactionToSave)
        return savedStockLot
    }

    /**
     * 株式ロットを一部または全部売却します。
     * 売却はFIFO（先入れ先出し）方式で行われ、最も古い購入取引から順に売却が割り当てられます。
     * 
     * もしcurrentUnitと売却Unitが同じであれば、IncomingHistoryとBenefitHistoryのレコードのstockLotIdをnullに設定し、
     * sellTransactionIdを設定します。
     * 
     * そうでない場合には、株式ロットに紐づくIncomingHistoryとBenefitHistoryのレコードを複製し、
     * 複製したレコードのstockLotIdをnullに設定し、sellTransactionIdを設定します。
     * 
     * ただしIncomingHistoryおよびBenefitHistoryのレコードが存在しない場合は、複製処理を行わないものとします。
     *
     * @param stockLotId 売却対象のStockLotのID
     * @param sellDto 売却情報（単元数、価格、手数料、取引日）を含むDTO
     * @throws IllegalArgumentException 指定されたIDのStockLotが存在しない場合
     * @throws IllegalArgumentException 売却単元数が現在の保有単元数を超えている場合
     */
    @Transactional
    fun sellStockLot(stockLotId: Int, sellDto: StockLotSellDto) {
        // stockLotIdに基づいてStockLotを取得
        val stockLot = stockLotRepository.findById(stockLotId)
            .orElseThrow { IllegalArgumentException("StockLot not found with id: $stockLotId") }
        // 売却単元数が現在の保有単元数を超えていないか確認
        if (sellDto.unit > stockLot.currentUnit) {
            throw IllegalArgumentException("Sell unit cannot be greater than current unit.")
        }

        //--- 超えていない場合はstockLotの売却処理を行う ---//

        // StockLotの現在のUnit数を取得
        var remainingStockLotUnit = stockLot.currentUnit

        // IncomingHistoryとBenefitHistoryのレコードを取得
        val incomingHistories = incomingHistoryRepository.findByStockLotId(stockLotId)
        val benefitHistories = benefitHistoryRepository.findByStockLotId(stockLotId)

        // stockLotに紐づくbuyTransactionを取得
        val buyTransactions = buyTransactionRepository.findByStockLotId(stockLotId)

        // buyTransactionsから1つ取り出す（最も古い取引をFIFOとして選択）//ここは改修の余地あり
        val buyTransaction = buyTransactions.minByOrNull { it.transactionDate }
            ?: throw IllegalArgumentException("No buy transactions found for stockLot id: $stockLotId")

        // sellTransactionを作成する
        val sellTransaction = SellTransaction(
            buyTransaction = buyTransaction, // 紐づくbuyTransactionを設定
            unit = sellDto.unit, // 売却単元数を設定
            price = sellDto.price, // 売却価格を設定
            fee = sellDto.fee, // 売却手数料を設定
            transactionDate = sellDto.transactionDate // 取引日を設定
        )
        val savedSellTransaction = sellTransactionService.create(sellTransaction)                

        /*
          [条件]: currentUnitと売却Unitが同じ場合
          [処理]:
          * sellTransactionIdを設定する
          * stockLotのcurrentUnitを0に更新
          * stockLotに紐づいているすべての、IncomingHistoryとBenefitHistoryのレコードのstockLotIdをnullに設定
        */
        // 全て売却
        if (stockLot.currentUnit - sellDto.unit == 0) {
            // stockLotのcurrentUnitを減らして更新
            val updatedStockLot = stockLot.copy(currentUnit = stockLot.currentUnit - sellDto.unit)
            stockLotRepository.save(updatedStockLot)

            // incomingHistoriesレコードのstockLotIdをnull、sellTransactionIdを設定する
            if (incomingHistories.isNotEmpty()) {
                incomingHistories.forEach { history ->
                    history.stockLot = null
                    history.sellTransaction = savedSellTransaction
                    incomingHistoryRepository.save(history)
                }
            }

            // benefitHistoriesレコードのstockLotIdをnull、sellTransactionIdを設定する
            if (benefitHistories.isNotEmpty()) {
                benefitHistories.forEach { history ->
                    history.stockLot = null
                    history.sellTransaction = savedSellTransaction
                    benefitHistoryRepository.save(history)
                }
            }

        // 売却後も残単元数がある場合
        } else {
            // stockLotのcurrentUnitを減らして更新
            val updatedStockLot = stockLot.copy(currentUnit = stockLot.currentUnit - sellDto.unit)
            stockLotRepository.save(updatedStockLot)

            // IncomingHistoryレコードを複製し、sellTransactionIdを設定
            if (incomingHistories.isNotEmpty()) {
                incomingHistories.forEach { history ->
                    val duplicatedHistory = IncomingHistory(
                        id = 0, // 新規レコードとして作成
                        stockLot = null,
                        sellTransaction = savedSellTransaction,
                        incoming = history.incoming,
                        paymentDate = history.paymentDate
                    )
                    incomingHistoryRepository.save(duplicatedHistory)
                }
            }

            // BenefitHistoryレコードを複製し、sellTransactionIdを設定
            if (benefitHistories.isNotEmpty()) {
            benefitHistories.forEach { history ->
                    val duplicatedHistory = BenefitHistory(
                        id = 0, // 新規レコードとして作成
                        stockLot = null,
                        sellTransaction = savedSellTransaction,
                        benefit = history.benefit,
                        paymentDate = history.paymentDate
                    )
                    benefitHistoryRepository.save(duplicatedHistory)
                }
            }
        }
    }
}
