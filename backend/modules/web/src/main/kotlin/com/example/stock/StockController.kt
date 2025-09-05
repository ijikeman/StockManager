package com.example.stock

import com.example.stock.model.Stock
import com.example.stock.service.StockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class StockController(
    private val stockService: StockService
) {
    /* 全てのStockを渡す */
    @GetMapping("/stocks")
    fun getStocks(): ResponseEntity<List<Stock>> {
        val stocks = stockService.findAll()
        return ResponseEntity(stocks, HttpStatus.OK)
    }

    /* 特定のStockを渡す */
    @GetMapping("/stocks/{id}")
    fun getStockById(@PathVariable id: Int): ResponseEntity<Stock> {
        val stock = stockService.findById(id)
        return if (stock != null) {
            ResponseEntity(stock, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
