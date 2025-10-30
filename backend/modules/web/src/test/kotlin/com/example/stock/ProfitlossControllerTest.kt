package com.example.stock

import com.example.stock.dto.ProfitlossDto
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
import com.example.stock.service.StockLotService
import com.example.stock.repository.BuyTransactionRepository
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDate

@WebMvcTest(ProfitlossController::class)
class ProfitlossControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var stockLotService: StockLotService

    @MockBean
    private lateinit var buyTransactionRepository: BuyTransactionRepository

    @Test
    fun `getProfitLoss should return list of stock names and buy prices`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock1 = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stock2 = Stock(id = 2, code = "5678", name = "Sony", currentPrice = 2000.0, minimalUnit = 100)
        val stockLots = listOf(
            StockLot(id = 1, owner = owner, stock = stock1, currentUnit = 10),
            StockLot(id = 2, owner = owner, stock = stock2, currentUnit = 5)
        )

        val buyTransaction1 = BuyTransaction(
            id = 1,
            stockLot = stockLots[0],
            unit = 10,
            price = BigDecimal("1200.25"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )
        val buyTransaction2 = BuyTransaction(
            id = 2,
            stockLot = stockLots[1],
            unit = 5,
            price = BigDecimal("2100.75"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        whenever(stockLotService.findAll()).thenReturn(stockLots)
        whenever(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction1))
        whenever(buyTransactionRepository.findByStockLotId(2)).thenReturn(listOf(buyTransaction2))

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[1].stockName").value("Sony"))
            .andExpect(jsonPath("$[0].purchasePrice").value(1200.25))
            .andExpect(jsonPath("$[1].purchasePrice").value(2100.75))
    }

    @Test
    fun `getProfitLoss should return empty list when no stock lots exist`() {
        whenever(stockLotService.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `getProfitLoss should handle stock lots without buy transactions`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10)

        whenever(stockLotService.findAll()).thenReturn(listOf(stockLot))
        whenever(buyTransactionRepository.findByStockLotId(1)).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[0].purchasePrice").value(0.0))
    }
}
