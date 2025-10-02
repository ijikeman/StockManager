package com.example.stock

import com.example.stock.dto.StockLotDTO
import com.example.stock.dto.StockLotDetailResponse
import com.example.stock.service.StockLotQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stock-lot")
class StockLotController(
    private val stockLotQueryService: StockLotQueryService,
) {
    @GetMapping
    fun getStockLots(): List<StockLotDTO> {
        return stockLotQueryService.findAllStockLots()
    }

    @GetMapping("/{id}")
    fun getStockLot(@PathVariable id: Long): StockLotDetailResponse {
        return stockLotQueryService.getStockLot(id)
    }
}