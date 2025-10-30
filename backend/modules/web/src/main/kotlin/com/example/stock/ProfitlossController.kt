package com.example.stock

import com.example.stock.dto.ProfitlossDto
import com.example.stock.service.StockLotService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfitlossController(private val stockLotService: StockLotService) {

    @GetMapping("/api/profitloss")
    fun getProfitLoss(): List<ProfitlossDto> {
        return stockLotService.findAll()
            .map { stockLot -> ProfitlossDto(stockCode = stockLot.stock.code, stockName = stockLot.stock.name) }
    }
}
