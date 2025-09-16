package com.example.stock

import com.example.stock.dto.StockLotDTO
import com.example.stock.service.StockLotQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stock-lot")
class StockLotController(
    private val stockLotQueryService: StockLotQueryService
) {
    @GetMapping
    fun getStockLots(): List<StockLotDTO> {
        return stockLotQueryService.findAllStockLots()
    }
}
