package com.example.stock

import com.example.stock.dto.StockLotDTO
import com.example.stock.dto.StockLotDetailResponse
import com.example.stock.dto.UpdateStockLotRequest
import com.example.stock.service.StockLotQueryService
import com.example.stock.service.StockLotService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stock-lot")
class StockLotController(
    private val stockLotQueryService: StockLotQueryService,
    private val stockLotService: StockLotService,
) {
    @GetMapping
    fun getStockLots(): List<StockLotDTO> {
        return stockLotQueryService.findAllStockLots()
    }

    @GetMapping("/{id}")
    fun getStockLot(@PathVariable id: Long): StockLotDetailResponse {
        return stockLotQueryService.getStockLot(id)
    }

    @PutMapping("/{id}")
    fun updateStockLot(
        @PathVariable id: Int,
        @RequestBody request: UpdateStockLotRequest,
    ) {
        stockLotService.updateStockLot(id, request.unit, request.isNisa)
    }

    @DeleteMapping("/{id}")
    fun deleteStockLot(@PathVariable id: Int) {
        stockLotService.deleteStockLot(id)
    }
}