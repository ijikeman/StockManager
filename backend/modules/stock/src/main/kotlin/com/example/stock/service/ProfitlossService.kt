package com.example.stock.service

import com.example.stock.dto.ProfitlossDto
import com.example.stock.repository.BuyTransactionRepository
import org.springframework.stereotype.Service

/**
 * 損益情報に関するビジネスロジックを処理するサービス。
 */
@Service
class ProfitlossService(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository
) {

    /**
     * すべての株式ロットから損益情報を取得します。
     * @return 損益情報のリスト
     */
    fun getProfitLoss(): List<ProfitlossDto> {
        return stockLotService.findAll()
            .map { stockLot ->
                // 株式ロットIDに紐づく最初の購入取引から価格を取得
                val buyTransaction = buyTransactionRepository.findByStockLotId(stockLot.id).firstOrNull()
                val purchasePrice = buyTransaction?.price?.toDouble() ?: 0.0
                ProfitlossDto(
                    stockCode = stockLot.stock.code,
                    stockName = stockLot.stock.name,
                    purchasePrice = purchasePrice
                )
            }
    }
}
