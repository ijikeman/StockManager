package com.example.stock

import com.example.stock.service.ProfitLossService
import com.example.stock.service.ProfitLossSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/pl")
class ProfitLossController(private val profitLossService: ProfitLossService) {

    @GetMapping("/{ownerId}")
    fun getProfitLossSummary(@PathVariable ownerId: Int): ProfitLossSummary {
        return profitLossService.calculateTotalProfitLoss(ownerId)
    }
}
