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

    /* Stockを登録する */
    @PostMapping("/stocks")
    fun createStock(@Validated @RequestBody stock: Stock): ResponseEntity<Stock> {
        val savedStock = stockService.save(stock)
        return ResponseEntity(savedStock, HttpStatus.CREATED)
    }

    /* 特定のStockを更新する */
    @PutMapping("/stocks/{id}")
    fun updateStock(@PathVariable id: Int, @Validated @RequestBody stock: Stock): ResponseEntity<Stock> {
        val stockToUpdate = stock.copy(id = id)
        val updatedStock = stockService.save(stockToUpdate)
        return ResponseEntity.ok(updatedStock)
    }

    /* 特定のStockを削除する */
    @DeleteMapping("/stocks/{id}")
    fun deleteStock(@PathVariable id: Int): ResponseEntity<Void> {
        stockService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    /* 特定のStockを更新する */
    @PostMapping("/stocks/{code}/update")
    fun updateStockPrice(@PathVariable code: String): ResponseEntity<Stock> {
        val updatedStock = stockService.updateStockPrice(code)
        return if (updatedStock != null) {
            ResponseEntity.ok(updatedStock)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /* 全てのStockを更新する */
    @PostMapping("/stocks/update-all")
    fun updateAllStockPrices(): ResponseEntity<List<Stock>> {
        val updatedStocks = stockService.updateAllStockPrices()
        return ResponseEntity.ok(updatedStocks)
    }
}
