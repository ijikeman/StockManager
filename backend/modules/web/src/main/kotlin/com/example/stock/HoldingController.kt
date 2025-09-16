package com.example.stock

import com.example.stock.dto.StockLotDTO
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
    fun getHoldingStockLots(): Map<Boolean, List<StockLotDTO>> {
        return stockLotQueryService.findHoldingStockLots()
    }

    @GetMapping("/owner/{ownerId}")
    fun getHoldingStockLotsByOwner(@PathVariable ownerId: Int): Map<Boolean, List<StockLotDTO>> {
        return stockLotQueryService.findHoldingStockLotsByOwner(ownerId)
    }

    @GetMapping("/{lotId}")
    fun getHoldingStockLot(@PathVariable lotId: Int): StockLotDTO {
        return stockLotQueryService.findStockLotById(lotId)
    }
}
