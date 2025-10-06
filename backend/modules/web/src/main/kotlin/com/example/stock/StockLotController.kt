package com.example.stock

import com.example.stock.model.StockLot
import com.example.stock.service.StockLotService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stocklot")
class StockLotController(
    private val StockLotService: StockLotService,
) {
    @GetMapping
    fun getStockLots(): List<StockLot> {
        return StockLotService.findAll()
    }

    /**
     * IDを指定してStockLotを取得します。
     * @param id 取得するStockLotのID
     * @return 見つかったStockLot。見つからない場合は404 Not Found。
     */
    @GetMapping("/{id}")
    fun getStockLot(@PathVariable id: Int): ResponseEntity<StockLot> {
        val stockLot = StockLotService.findById(id)
        return if (stockLot != null) {
            ResponseEntity.ok(stockLot)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
