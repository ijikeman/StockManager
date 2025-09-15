package com.example.stock

import com.example.stock.model.StockLot
import com.example.stock.service.StockLotQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/holding")
class HoldingController(
    private val stockLotQueryService: StockLotQueryService
) {
    @GetMapping
    fun getHoldingStockLots(): Map<Boolean, List<StockLot>> {
        return stockLotQueryService.findHoldingStockLots()
    }

    @GetMapping("/{ownerId}")
    fun getHoldingStockLotsByOwner(@PathVariable ownerId: Int): Map<Boolean, List<StockLot>> {
        return stockLotQueryService.findHoldingStockLotsByOwner(ownerId)
    }
}
