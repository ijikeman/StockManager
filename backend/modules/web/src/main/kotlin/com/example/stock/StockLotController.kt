package com.example.stock

import com.example.stock.dto.StockLotAddDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.model.BuyTransaction
import com.example.stock.model.StockLot
import com.example.stock.service.OwnerService
import com.example.stock.service.StockLotService
import com.example.stock.service.StockService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stocklot")
class StockLotController(
    private val stockLotService: StockLotService,
    private val ownerService: OwnerService,
    private val stockService: StockService,
) {
    @GetMapping
    fun getStockLots(): List<StockLotResponseDto> {
        return stockLotService.findAllWithAveragePrice()
    }

    /**
     * IDを指定してStockLotを取得します。
     * @param id 取得するStockLotのID
     * @return 見つかったStockLot。見つからない場合は404 Not Found。
     */
    @GetMapping("/{id}")
    fun getStockLot(@PathVariable id: Int): ResponseEntity<StockLotResponseDto> {
        val stockLotDto = stockLotService.findByIdWithAveragePrice(id)
        return if (stockLotDto != null) {
            ResponseEntity.ok(stockLotDto)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/add")
    fun addStockLot(@RequestBody stockLotAddDto: StockLotAddDto): ResponseEntity<Any> {
        val owner = ownerService.findById(stockLotAddDto.ownerId)
            ?: return ResponseEntity.badRequest().body("Owner not found with id: ${stockLotAddDto.ownerId}")
        val stock = stockService.findById(stockLotAddDto.stockId)
            ?: return ResponseEntity.badRequest().body("Stock not found with id: ${stockLotAddDto.stockId}")

        // StockLotServiceのcreateStockLotAndBuyTransactionがBuyTransactionを要求するため、
        // 仮のStockLotを持つBuyTransactionを作成する
        val buyTransaction = BuyTransaction(
            stockLot = StockLot(owner = owner, stock = stock, currentUnit = stockLotAddDto.unit),
            unit = stockLotAddDto.unit,
            price = stockLotAddDto.price,
            fee = stockLotAddDto.fee,
            isNisa = stockLotAddDto.isNisa,
            transactionDate = stockLotAddDto.transactionDate,
        )

        val createdStockLot = stockLotService.createStockLotAndBuyTransaction(
            owner = owner,
            stock = stock,
            currentUnit = stockLotAddDto.unit,
            buyTransaction = buyTransaction,
        )

        // 作成したStockLotを元に、平均価格を含むDTOを生成して返す
        val responseDto = stockLotService.findByIdWithAveragePrice(createdStockLot.id)
            ?: return ResponseEntity.internalServerError().body("Could not retrieve created stock lot with average price")

        return ResponseEntity.ok(responseDto)
    }
}