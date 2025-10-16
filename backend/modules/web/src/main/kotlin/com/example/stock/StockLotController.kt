package com.example.stock

import com.example.stock.dto.StockLotAddDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.dto.StockLotSellDto
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stocklot")
class StockLotController(
    private val stockLotService: StockLotService,
    private val ownerService: OwnerService,
    private val stockService: StockService,
) {
    @GetMapping
    /**
     * 在庫ロット（StockLot）の一覧を取得します。
     * @param ownerId 所有者ID（オプション）。指定された場合は、その所有者の在庫ロットのみを取得します。
     * @return 平均価格情報を含む在庫ロットのリスト。ownerIdが指定された場合は、そのオーナーの在庫ロットのみ返します。
     */
    fun getStockLots(@RequestParam(required = false) ownerId: Int?): List<StockLotResponseDto> {
        return if (ownerId != null) {
            stockLotService.findByOwnerIdWithAveragePrice(ownerId)
        } else {
            stockLotService.findAllWithAveragePrice()
        }
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
    // 引数はStockLotAddDtoの型で取得
    fun addStockLot(@RequestBody stockLotAddDto: StockLotAddDto): ResponseEntity<Any> {
        // ownerIDからownerを取得
        val owner = ownerService.findById(stockLotAddDto.ownerId)
            // ownerが見つからない場合、400 Bad Requestを返す
            ?: return ResponseEntity.badRequest().body("Owner not found with id: ${stockLotAddDto.ownerId}")
        // stockIDからstockを取得
        val stock = stockService.findById(stockLotAddDto.stockId)
            // stockが見つからない場合、400 Bad Requestを返す
            ?: return ResponseEntity.badRequest().body("Stock not found with id: ${stockLotAddDto.stockId}")

        val createdStockLot = stockLotService.createStockLot(
            owner = owner,
            stock = stock,
            unit = stockLotAddDto.unit,
            price = stockLotAddDto.price,
            fee = stockLotAddDto.fee,
            isNisa = stockLotAddDto.isNisa,
            transactionDate = stockLotAddDto.transactionDate
        )
        val responseDto = stockLotService.findByIdWithAveragePrice(createdStockLot.id)
            ?: return ResponseEntity.internalServerError().body("Could not retrieve created stock lot with average price")

        return ResponseEntity.ok(responseDto)

        // // StockLotServiceのcreateStockLotAndBuyTransactionがBuyTransactionを要求するため、
        // // 仮のStockLotを持つBuyTransactionを作成する
        // val buyTransaction = BuyTransaction(
        //     stockLot = StockLot(owner = owner, stock = stock, currentUnit = stockLotAddDto.unit),
        //     unit = stockLotAddDto.unit,
        //     price = stockLotAddDto.price,
        //     fee = stockLotAddDto.fee,
        //     isNisa = stockLotAddDto.isNisa,
        //     transactionDate = stockLotAddDto.transactionDate,
        // )
        // // stockLotを作成する
        // val createdStockLot = stockLotService.createStockLotAndBuyTransaction(
        //     owner = owner,
        //     stock = stock,
        //     currentUnit = stockLotAddDto.unit,
        //     buyTransaction = buyTransaction,
        // )

        // // 作成したStockLotを元に、平均価格を含むDTOを生成して返す
        // val responseDto = stockLotService.findByIdWithAveragePrice(createdStockLot.id)
        //     ?: return ResponseEntity.internalServerError().body("Could not retrieve created stock lot with average price")

        // return ResponseEntity.ok(responseDto)
    }

    /**
     * 指定されたIDの在庫ロット（StockLot）を売却します。
     * @param id 売却する在庫ロットのID
     * @param sellDto 売却情報（売却単位数、価格、手数料など）を含むDTO
     * @return 成功時は200 OK、失敗時（在庫不足など）は400 Bad Request
     * @throws IllegalArgumentException 売却できない条件の場合（在庫不足など）にスローされます
     */
    @PostMapping("/{id}/sell")
    fun sellStockLot(@PathVariable id: Int, @RequestBody sellDto: StockLotSellDto): ResponseEntity<Any> {
        return try {
            stockLotService.sellStockLot(id, sellDto)
            ResponseEntity.ok().build()
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(e.message)
        }
    }
}
