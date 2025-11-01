package com.example.stock.service

import com.example.stock.dto.ProfitlossDto
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.SellTransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 損益情報に関するビジネスロジックを処理するサービス。
 */
@Service
class ProfitlossService(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository,
    private val sellTransactionRepository: SellTransactionRepository
) {

    /**
     * 株式ロットから損益情報を取得します。
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
        }.filter { it.currentUnit > 0 }
        
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
        val buyTransactionIds = buyTransactionsMap.values.flatten().map { it.id }
        val sellTransactionsMap = if (buyTransactionIds.isNotEmpty()) {
            buyTransactionIds.flatMap { buyTransactionId ->
                sellTransactionRepository.findByBuyTransactionId(buyTransactionId)
                    .map { buyTransactionId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        
        // 各購入取引とその売却取引のペアから損益を計算
        return buyTransactionsMap.flatMap { (stockLotId, buyTransactions) ->
            val stockLot = stockLots.first { it.id == stockLotId }
            buyTransactions.flatMap { buyTransaction ->
                val sellTransactions = sellTransactionsMap[buyTransaction.id] ?: emptyList()
                if (sellTransactions.isEmpty()) {
                    // 売却取引がない場合は、購入情報のみを返す
                    listOf(
                        ProfitlossDto(
                            stockCode = stockLot.stock.code,
                            stockName = stockLot.stock.name,
                            purchasePrice = buyTransaction.price.toDouble(),
                            sellPrice = null,
                            sellUnit = null,
                            profitLoss = null,
                            buyTransactionDate = buyTransaction.transactionDate,
                            sellTransactionDate = null
                        )
                    )
                } else {
                    // 各売却取引について損益を計算
                    sellTransactions.map { sellTransaction ->
                        // 損益 = (売却価格 - 購入価格) * 単元数 * 最小単元 - 購入手数料 - 売却手数料
                        val minimalUnit = stockLot.stock.minimalUnit
                        val profitLoss = (sellTransaction.price - buyTransaction.price) * 
                                        BigDecimal(sellTransaction.unit) * 
                                        BigDecimal(minimalUnit) - 
                                        buyTransaction.fee - 
                                        sellTransaction.fee
                        
                        ProfitlossDto(
                            stockCode = stockLot.stock.code,
                            stockName = stockLot.stock.name,
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
    }
}
