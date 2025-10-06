package com.example.stock

import com.example.stock.model.StockLot
import com.example.stock.service.StockLotService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stock-lot")
class StockLotController(
    private val StockLotService: StockLotService,
) {
    @GetMapping
    fun getStockLots(): List<StockLot> {
        return StockLotService.findAllStockLots()
    }

    @GetMapping("/{id}")
    fun getStockLot(@PathVariable id: Int): StockLot {
        return StockLotService.findStockLotById(id)
    }
}
