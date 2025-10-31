package com.example.stock

import com.example.stock.dto.ProfitlossDto
import com.example.stock.service.ProfitlossService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/profitloss")
class ProfitlossController(
    private val profitlossService: ProfitlossService
) {

    @GetMapping
    fun getProfitLoss(): List<ProfitlossDto> {
        return profitlossService.getProfitLoss()
    }
}
