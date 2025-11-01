package com.example.stock.service

import com.example.stock.dto.ProfitlossDto
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.BenefitHistoryRepository
import org.springframework.stereotype.Service

/**
 * 損益情報に関するビジネスロジックを処理するサービス。
 */
@Service
class ProfitlossService(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository
) {

    /**
     * 株式ロットから損益情報を取得します。
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
        
        // N+1クエリ問題を回避: すべての株式ロットIDに対して配当履歴を一括取得
        val incomingHistoryMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                incomingHistoryRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        
        // N+1クエリ問題を回避: すべての株式ロットIDに対して優待履歴を一括取得
        val benefitHistoryMap = if (stockLotIds.isNotEmpty()) {
            stockLotIds.flatMap { stockLotId ->
                benefitHistoryRepository.findByStockLotId(stockLotId)
                    .map { stockLotId to it }
            }.groupBy({ it.first }, { it.second })
        } else {
            emptyMap()
        }
        
        return stockLots.map { stockLot ->
            // 株式ロットIDに紐づく最初の購入取引から価格を取得
            val buyTransaction = buyTransactionsMap[stockLot.id]?.firstOrNull()
            val purchasePrice = buyTransaction?.price?.toDouble() ?: 0.0
            
            // 株式ロットIDに紐づく配当履歴の合計を計算
            val totalDividend = incomingHistoryMap[stockLot.id]
                ?.sumOf { it.incoming.toDouble() } ?: 0.0
            
            // 株式ロットIDに紐づく優待履歴の合計を計算
            val totalBenefit = benefitHistoryMap[stockLot.id]
                ?.sumOf { it.benefit.toDouble() } ?: 0.0
            
            ProfitlossDto(
                stockCode = stockLot.stock.code,
                stockName = stockLot.stock.name,
                purchasePrice = purchasePrice,
                totalDividend = totalDividend,
                totalBenefit = totalBenefit
            )
        }
    }
}
