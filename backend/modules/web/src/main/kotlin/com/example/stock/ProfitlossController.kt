package com.example.stock

import com.example.stock.dto.ProfitlossStockLotDto
import com.example.stock.dto.ProfitlossDto
import com.example.stock.service.ProfitlossService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profitloss")
class ProfitlossController(
    private val profitlossService: ProfitlossService
) {
    @GetMapping
    fun getProfitStockLotLoss(@RequestParam(required = false) ownerId: Int?): List<ProfitlossStockLotDto> {
        return profitlossService.getProfitStockLotLoss(ownerId)
    }
    
    @GetMapping("/realized")
    fun getProfitLoss(@RequestParam(required = false) ownerId: Int?): List<ProfitlossDto> {
        return profitlossService.getProfitLoss(ownerId)
    }
}
