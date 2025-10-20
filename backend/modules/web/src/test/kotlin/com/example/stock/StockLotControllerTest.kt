package com.example.stock

import com.example.stock.dto.OwnerDto
import com.example.stock.dto.StockDto
import com.example.stock.dto.StockLotAddDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.dto.StockLotSellDto
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.service.OwnerService
import com.example.stock.service.StockLotService
import com.example.stock.service.StockService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(StockLotController::class)
class StockLotControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var stockLotService: StockLotService

    @MockBean
    private lateinit var ownerService: OwnerService

    @MockBean
    private lateinit var stockService: StockService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val ownerDto = OwnerDto(id = 1, name = "Test Owner")
    private val stockDto = StockDto(id = 1, code = "1234", name = "Test Stock", currentPrice = 1200.0, minimalUnit = 100)
    private val purchaseDate = LocalDate.now()

    @Test
    fun `getStockLots should return a list of stock lots`() {
        val stockLotsDto = listOf(
            StockLotResponseDto(id = 1, owner = ownerDto, stock = stockDto, currentUnit = 10, averagePrice = BigDecimal("1000.00"), purchaseDate = purchaseDate)
        )

        whenever(stockLotService.findAllWithAveragePrice()).thenReturn(stockLotsDto)

        mockMvc.perform(get("/api/stocklot"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].owner.name").value("Test Owner"))
            .andExpect(jsonPath("$[0].stock.name").value("Test Stock"))
            .andExpect(jsonPath("$[0].averagePrice").value(1000.00))
            .andExpect(jsonPath("$[0].purchaseDate").value(purchaseDate.toString()))
    }

    @Test
    fun `getStockLot should return a single stock lot`() {
        val stockLotDto = StockLotResponseDto(id = 1, owner = ownerDto, stock = stockDto, currentUnit = 10, averagePrice = BigDecimal("1000.00"), purchaseDate = purchaseDate)

        whenever(stockLotService.findByIdWithAveragePrice(1)).thenReturn(stockLotDto)

        mockMvc.perform(get("/api/stocklot/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.owner.name").value("Test Owner"))
            .andExpect(jsonPath("$.averagePrice").value(1000.00))
            .andExpect(jsonPath("$.purchaseDate").value(purchaseDate.toString()))
    }

    @Test
    fun `addStockLot should create stock lot and return it`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100, currentPrice = 1200.0)
        val stockLotAddDto = StockLotAddDto(
            ownerId = 1,
            stockId = 1,
            unit = 1,
            price = BigDecimal("1000"),
            fee = BigDecimal("100"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )
        val createdStockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 1)
        val responseDto = StockLotResponseDto(id = 1, owner = ownerDto, stock = stockDto, currentUnit = 1, averagePrice = BigDecimal("1100.00"), purchaseDate = stockLotAddDto.transactionDate)

        whenever(ownerService.findById(1)).thenReturn(owner)
        whenever(stockService.findById(1)).thenReturn(stock)
        whenever(stockLotService.createStockLot(
            owner = eq(owner),
            stock = eq(stock),
            unit = eq(stockLotAddDto.unit),
            price = eq(stockLotAddDto.price),
            fee = eq(stockLotAddDto.fee),
            isNisa = eq(stockLotAddDto.isNisa),
            transactionDate = eq(stockLotAddDto.transactionDate)
        )).thenReturn(createdStockLot)
        whenever(stockLotService.findByIdWithAveragePrice(createdStockLot.id)).thenReturn(responseDto)

        mockMvc.perform(
            post("/api/stocklot/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockLotAddDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(responseDto.id))
            .andExpect(jsonPath("$.owner.name").value(responseDto.owner.name))
            .andExpect(jsonPath("$.stock.name").value(responseDto.stock.name))
            .andExpect(jsonPath("$.currentUnit").value(responseDto.currentUnit))
            .andExpect(jsonPath("$.averagePrice").value(1100.00))
            .andExpect(jsonPath("$.purchaseDate").value(responseDto.purchaseDate.toString()))
    }

    @Test
    fun `addStockLot should return bad request if owner not found`() {
        val stockLotAddDto = StockLotAddDto(
            ownerId = 999, // Non-existent owner
            stockId = 1,
            unit = 1,
            price = BigDecimal("1000"),
            fee = BigDecimal("100"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        whenever(ownerService.findById(999)).thenReturn(null)

        mockMvc.perform(
            post("/api/stocklot/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockLotAddDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `addStockLot should return bad request if stock not found`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stockLotAddDto = StockLotAddDto(
            ownerId = 1,
            stockId = 999, // Non-existent stock
            unit = 1,
            price = BigDecimal("1000"),
            fee = BigDecimal("100"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        whenever(ownerService.findById(1)).thenReturn(owner)
        whenever(stockService.findById(999)).thenReturn(null)

        mockMvc.perform(
            post("/api/stocklot/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockLotAddDto))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `sellStockLot should return ok on success`() {
        val sellDto = StockLotSellDto(
            unit = 5,
            price = BigDecimal("1200"),
            fee = BigDecimal("120"),
            transactionDate = LocalDate.now()
        )

        mockMvc.perform(
            post("/api/stocklot/1/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellDto))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `sellStockLot should return bad request on failure`() {
        val sellDto = StockLotSellDto(
            unit = 15, // Assume this is more than available
            price = BigDecimal("1200"),
            fee = BigDecimal("120"),
            transactionDate = LocalDate.now()
        )

        whenever(stockLotService.sellStockLot(any(), any())).thenThrow(IllegalArgumentException("Not enough units"))

        mockMvc.perform(
            post("/api/stocklot/1/sell")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sellDto))
        )
            .andExpect(status().isBadRequest)
    }
}