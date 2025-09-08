package com.example.stock

import com.example.stock.model.Holding
import com.example.stock.service.HoldingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class HoldingController(
    private val holdingService: HoldingService
) {
    /* 全てのHoldingを渡す */
    @GetMapping("/holding")
    fun getHoldings(): ResponseEntity<List<Holding>> {
        val holdings = holdingService.findAll()
        return ResponseEntity(holdings, HttpStatus.OK)
    }
}
