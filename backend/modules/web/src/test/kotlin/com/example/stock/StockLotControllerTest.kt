package com.example.stock

import com.example.stock.dto.StockLotAddDto
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.service.OwnerService
import com.example.stock.service.StockLotService
import com.example.stock.service.StockService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
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
import java.util.Optional

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

    @Test
    fun `getStockLots should return a list of stock lots`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
        val stockLots = listOf(StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10))

        whenever(stockLotService.findAll()).thenReturn(stockLots)

        mockMvc.perform(get("/api/stocklot"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].owner.name").value("Test Owner"))
            .andExpect(jsonPath("$[0].stock.name").value("Test Stock"))
    }

    @Test
    fun `getStockLot should return a single stock lot`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10)

        whenever(stockLotService.findById(1)).thenReturn(stockLot)

        mockMvc.perform(get("/api/stocklot/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.owner.name").value("Test Owner"))
    }

    @Test
    fun `addStockLot should create stock lot and return it`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
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

        whenever(ownerService.findById(1)).thenReturn(owner)
        whenever(stockService.findById(1)).thenReturn(stock)
        whenever(stockLotService.createStockLotAndBuyTransaction(any(), any(), any(), any())).thenReturn(createdStockLot)

        mockMvc.perform(
            post("/api/stocklot/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(stockLotAddDto))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(createdStockLot.id))
            .andExpect(jsonPath("$.owner.id").value(owner.id))
            .andExpect(jsonPath("$.owner.name").value(owner.name))
            .andExpect(jsonPath("$.stock.id").value(stock.id))
            .andExpect(jsonPath("$.stock.name").value(stock.name))
            .andExpect(jsonPath("$.currentUnit").value(createdStockLot.currentUnit))
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
}