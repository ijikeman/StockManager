package com.example.stock

import com.example.stock.model.StockLot
import com.example.stock.service.StockLotService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfitlossController(private val stockLotService: StockLotService) {

    @GetMapping("/api/profitloss")
    fun getProfitLoss(): List<StockLot> {
        return stockLotService.findAll()
    }
}
