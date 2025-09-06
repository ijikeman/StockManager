package com.example.stock

import com.example.stock.model.Stock
import com.example.stock.service.StockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class StockController(
    private val stockService: StockService
) {
    /* 全てのStockを渡す */
    @GetMapping("/stock")
    fun getStocks(): ResponseEntity<List<Stock>> {
        val stocks = stockService.findAll()
        return ResponseEntity(stocks, HttpStatus.OK)
    }

    /* 特定のStockを渡す */
    @GetMapping("/stock/{id}")
    fun getStockById(@PathVariable id: Int): ResponseEntity<Stock> {
        val stock = stockService.findById(id)
        return if (stock != null) {
            ResponseEntity(stock, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    /* Stockを登録する */
    @PostMapping("/stock")
    fun createStock(@Validated @RequestBody stock: Stock): ResponseEntity<Stock> {
        val savedStock = stockService.save(stock)
        return ResponseEntity(savedStock, HttpStatus.CREATED)
    }

    /* 特定のStockを更新する */
    @PutMapping("/stock/{id}")
    fun updateStock(@PathVariable id: Int, @Validated @RequestBody stock: Stock): ResponseEntity<Stock> {
        val stockToUpdate = stock.copy(id = id)
        val updatedStock = stockService.save(stockToUpdate)
        return ResponseEntity.ok(updatedStock)
    }

    /* 特定のStockを削除する */
    @DeleteMapping("/stock/{id}")
    fun deleteStock(@PathVariable id: Int): ResponseEntity<Void> {
        stockService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
