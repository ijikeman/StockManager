package com.example.stock.service

import com.example.stock.dto.ProfitlossStockLotDto
import com.example.stock.dto.ProfitlossDto
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.SellTransactionRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.model.IncomingHistory
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 損益情報に関するビジネスロジックを処理するサービス。
 */
@Service
class ProfitlossService(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository,
    private val sellTransactionRepository: SellTransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository
) {

    /**
     * 株式ロットから損益情報を取得します。
     * ownerIdに紐づく全stocklotを検索し、関連buytransactionを検索し、
     * さらにselltransactionを検索し、売却損益を計算する。
     * @param ownerId 所有者ID（オプション）。指定された場合は、その所有者の株式ロットのみを取得します。
     * @return 損益情報のリスト
     */
    fun getProfitStockLotLoss(ownerId: Int? = null): List<ProfitlossStockLotDto> {
        val stockLots = if (ownerId != null) {
            stockLotService.findByOwnerId(ownerId)
        } else {
            stockLotService.findAll()
        }.filter { it.currentUnit > 0 } // 現在の単元数が0より大きい株式ロットのみ対象
        
        // N+1クエリ問題を回避: 
        val stockLotIds = stockLots.map { it.id }
        // すべての株式ロットIDに対して購入取引を一括取得
        val buyTransactionsMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                buyTransactionRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        
        // すべての購入取引IDに対して配当金履歴の一括取得し総配当金額をDtoに格納
        val buyTransactionIds = buyTransactionsMap.values.flatten().mapNotNull { it.id }

        // 一括取得: buyTransactionId -> List<IncomingHistory>
        val incomingHistoriesMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                incomingHistoryRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap<Int, List<IncomingHistory>>()
        }

        // 購入取引ごとの総配当金額 (BigDecimal) を算出
        val incomingTotalsMap: Map<Int, BigDecimal> = incomingHistoriesMap.mapValues { (_, incomes) ->
            incomes.fold(BigDecimal.ZERO) { acc, d ->
            acc + (d.incoming ?: BigDecimal.ZERO)
            }
        }
        // Dtoへの格納は後続処理で incomingTotalsMap を参照して行う

        // すべての購入取引IDに対して優待履歴の一括取得
        val benefitHistoriesMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                benefitHistoryRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }

        // 購入取引ごとの総優待金額を算出
        val benefitTotalsMap: Map<Int, BigDecimal> = benefitHistoriesMap.mapValues { (_, benefits) ->
            benefits.fold(BigDecimal.ZERO) { acc, b ->
                acc + (b.benefit ?: BigDecimal.ZERO)
            }
        }

        // DTOのリストを作成
        return stockLots.map { stockLot ->
            val buyTransactions = buyTransactionsMap[stockLot.id] ?: emptyList()
            // 各購入取引の配当金額と優待金額を合計
            val totalIncoming = buyTransactions.fold(BigDecimal.ZERO) { acc, bt ->
                acc + (incomingTotalsMap[bt.id] ?: BigDecimal.ZERO)
            }
            val totalBenefit = buyTransactions.fold(BigDecimal.ZERO) { acc, bt ->
                acc + (benefitTotalsMap[bt.id] ?: BigDecimal.ZERO)
            }
            // 最初の購入取引日を取得
            val firstBuyTransaction = buyTransactions.minByOrNull { it.transactionDate }

            ProfitlossStockLotDto(
                stockCode = stockLot.stock.code,
                stockName = stockLot.stock.name,
                minimalUnit = stockLot.stock.minimalUnit,
                purchasePrice = firstBuyTransaction?.price?.toDouble() ?: 0.0,
                currentPrice = stockLot.stock.currentPrice,
                currentUnit = stockLot.currentUnit,
                totalIncoming = totalIncoming,
                totalBenefit = totalBenefit,
                buyTransactionDate = firstBuyTransaction?.transactionDate
            )
        }
    }

    /**
     * 売却取引から損益情報を取得します。
     * ownerIdに紐づく全stocklotを検索し、関連buytransactionを検索し、
     * さらにselltransactionを検索し、売却損益を計算する。
     * @param ownerId 所有者ID（オプション）。指定された場合は、その所有者の株式ロットのみを取得します。
     * @return 損益情報のリスト
     */
    fun getProfitLoss(ownerId: Int? = null): List<ProfitlossDto> {
        val stockLots = if (ownerId != null) {
            stockLotService.findByOwnerId(ownerId)
        } else {
            stockLotService.findAll()
        }
        
        // N+1クエリ問題を回避: すべての株式ロットIDに対して購入取引を一括取得
        val stockLotIds = stockLots.map { it.id }
        val buyTransactionsMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                buyTransactionRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        
        // すべての購入取引IDに対して売却取引を一括取得
        val buyTransactionIds = buyTransactionsMap.values.flatten().mapNotNull { it.id }
        val sellTransactionsMap = if (buyTransactionIds.isNotEmpty()) {
            buyTransactionIds.flatMap { buyTransactionId ->
                sellTransactionRepository.findByBuyTransactionId(buyTransactionId)
                    .map { buyTransactionId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }

        // すべての売却取引IDに対して配当金履歴の一括取得し総配当金額をDtoに格納
        val sellTransactionIds = sellTransactionsMap.values.flatten().mapNotNull { it.id }

        // 一括取得: sellTransactionId -> List<IncomingHistory>
        val incomingHistoriesMap = if (sellTransactionIds.isNotEmpty()) {
            sellTransactionIds.flatMap { sellTransactionId ->
                incomingHistoryRepository.findBySellTransactionId(sellTransactionId)
                    .map { sellTransactionId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap<Int, List<IncomingHistory>>()
        }
        // 購入取引ごとの総配当金額 (BigDecimal) を算出
        val incomingTotalsMap: Map<Int, BigDecimal> = incomingHistoriesMap.mapValues { (_, incomes) ->
            incomes.fold(BigDecimal.ZERO) { acc, d ->
            acc + (d.incoming ?: BigDecimal.ZERO)
            }
        }
        // Dtoへの格納は後続処理で incomingTotalsMap を参照して行う

        // すべての売却取引IDに対して優待履歴の一括取得
        val benefitHistoriesMap = if (sellTransactionIds.isNotEmpty()) {
            sellTransactionIds.flatMap { sellTransactionId ->
                benefitHistoryRepository.findBySellTransactionId(sellTransactionId)
                    .map { sellTransactionId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        // 購入取引ごとの総優待金額を算出
        val benefitTotalsMap: Map<Int, BigDecimal> = benefitHistoriesMap.mapValues { (_, benefits) ->
            benefits.fold(BigDecimal.ZERO) { acc, b ->
                acc + (b.benefit ?: BigDecimal.ZERO)
            }
        }

        // DTOのリストを作成
        return stockLots.map { stockLot ->
            // 損益計算: (売却価格 - 購入価格) * 単元数 * 最小単元数 - 購入手数料 - 売却手数料
            val ProfitStockLotlossDto(
                    stockCode = stockLot.stock.code,
                    stockName = stockLot.stock.name,
                    minimalUnit = stockLot.stock.minimalUnit,
                    purchasePrice = buyTransaction.price.toDouble(),
                    sellPrice = sellTransaction.price.toDouble(),
                    sellUnit = sellTransaction.unit,
                    profitLoss = profitLoss,
                    buyTransactionDate = buyTransaction.transactionDate,
                    sellTransactionDate = sellTransaction.transactionDate
                )
        }
        }
    }
}