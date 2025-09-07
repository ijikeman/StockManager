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

    /* 特定のStockを更新する */
    @PostMapping("/stock/{code}/update")
    fun updateStockPrice(@PathVariable code: String): ResponseEntity<Stock> {
        val updatedStock = stockService.updateStockPrice(code)
        return if (updatedStock != null) {
            ResponseEntity.ok(updatedStock)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /* 全てのStockを更新する */
    @PostMapping("/stock/update-all")
    fun updateAllStockPrices(): ResponseEntity<List<Stock>> {
        val updatedStocks = stockService.updateAllStockPrices()
        return ResponseEntity.ok(updatedStocks)
    }

    /**
     * 指定された株式コードの銘柄名を取得します。
     *
     * @param code 株式コード。
     * @return 銘柄名。見つからない場合は404 Not Found。
     */
    @GetMapping("/stock/name/{code}")
    fun getStockName(@PathVariable code: String): ResponseEntity<String> {
        val stockName = stockService.fetchStockName(code)
        return if (stockName != null) {
            ResponseEntity.ok(stockName)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
