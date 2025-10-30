package com.example.stock

import com.example.stock.dto.ProfitlossDto
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.service.StockLotService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ProfitlossController::class)
class ProfitlossControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var stockLotService: StockLotService

    @Test
    fun `getProfitLoss should return list of stock names only`() {
        val owner = Owner(id = 1, name = "Test Owner")
        val stock1 = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stock2 = Stock(id = 2, code = "5678", name = "Sony", currentPrice = 2000.0, minimalUnit = 100)
        val stockLots = listOf(
            StockLot(id = 1, owner = owner, stock = stock1, currentUnit = 10),
            StockLot(id = 2, owner = owner, stock = stock2, currentUnit = 5)
        )

        whenever(stockLotService.findAll()).thenReturn(stockLots)

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[1].stockName").value("Sony"))
    }

    @Test
    fun `getProfitLoss should return empty list when no stock lots exist`() {
        whenever(stockLotService.findAll()).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }
}
