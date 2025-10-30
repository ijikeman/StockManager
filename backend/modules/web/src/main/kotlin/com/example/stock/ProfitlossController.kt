package com.example.stock

import com.example.stock.dto.ProfitlossDto
import com.example.stock.service.StockLotService
import com.example.stock.repository.BuyTransactionRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profitloss")
class ProfitlossController(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository
) {

    @GetMapping
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
