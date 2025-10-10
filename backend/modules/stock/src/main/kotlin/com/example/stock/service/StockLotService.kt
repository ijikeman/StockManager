package com.example.stock.service

import com.example.stock.dto.OwnerDto
import com.example.stock.dto.StockDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
    private val buyTransactionService: BuyTransactionService,
    private val buyTransactionRepository: BuyTransactionRepository,
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
     * 単一の株式ロットを作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param currentUnit 現在の単元数
     * @return 作成されたStockLot
     */
    fun createStockLot(
        owner: Owner,
        stock: Stock,
        currentUnit: Int,
    ): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit,
        )
        return stockLotRepository.save(stockLot)
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
}
