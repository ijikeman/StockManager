package com.example.stock

import com.example.stock.dto.HoldingDTO
import com.example.stock.service.HoldingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/holdings")
class HoldingController(
    private val holdingService: HoldingService
) {

    @GetMapping
    fun getHoldings(): List<HoldingDTO> {
        return holdingService.getHoldings()
    }
}
