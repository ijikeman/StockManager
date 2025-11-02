package com.example.stock

import com.example.stock.dto.ProfitlossStockLotDto
import com.example.stock.service.ProfitlossService
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
    private lateinit var profitlossService: ProfitlossService

    @Test
    fun `getProfitStockLotLoss should return list of stock lots with complete information`() {
        val date = LocalDate.of(2025, 1, 1)
        val profitlossList = listOf(
            ProfitlossStockLotDto(
                stockCode = "1234",
                stockName = "Toyota",
                minimal_unit = 1,
                purchasePrice = 1200.25,
                currentPrice = 1300.0,
                currentUnit = 100,
                totalIncoming = BigDecimal("50000"),
                totalBenefit = BigDecimal("2000"),
                buyTransactionDate = date
            ),
            ProfitlossStockLotDto(
                stockCode = "5678",
                stockName = "Sony",
                minimal_unit = 1,
                purchasePrice = 2100.75,
                currentPrice = 2200.0,
                currentUnit = 50,
                totalIncoming = BigDecimal("25000"),
                totalBenefit = BigDecimal("1000"),
                buyTransactionDate = date
            )
        )

        whenever(profitlossService.getProfitStockLotLoss(null)).thenReturn(profitlossList)

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockCode").value("1234"))
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[0].minimal_unit").value(1))
            .andExpect(jsonPath("$[0].purchasePrice").value(1200.25))
            .andExpect(jsonPath("$[0].currentPrice").value(1300.0))
            .andExpect(jsonPath("$[0].currentUnit").value(100))
            .andExpect(jsonPath("$[0].totalIncoming").value(50000))
            .andExpect(jsonPath("$[0].totalBenefit").value(2000))
            .andExpect(jsonPath("$[0].buyTransactionDate").value("2025-01-01"))
            .andExpect(jsonPath("$[1].stockCode").value("5678"))
            .andExpect(jsonPath("$[1].stockName").value("Sony"))
    }

    @Test
    fun `getProfitStockLotLoss should return empty list when no stock lots exist`() {
        whenever(profitlossService.getProfitStockLotLoss(null)).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `getProfitStockLotLoss should handle stock lots with minimal information`() {
        val profitlossList = listOf(
            ProfitlossStockLotDto(
                stockCode = "1234",
                stockName = "Toyota",
                minimal_unit = 1,
                purchasePrice = 1200.25,
                currentPrice = null,
                currentUnit = null,
                totalIncoming = null,
                totalBenefit = null,
                buyTransactionDate = null
            )
        )

        whenever(profitlossService.getProfitStockLotLoss(null)).thenReturn(profitlossList)

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockCode").value("1234"))
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[0].minimal_unit").value(1))
            .andExpect(jsonPath("$[0].purchasePrice").value(1200.25))
            .andExpect(jsonPath("$[0].currentPrice").doesNotExist())
            .andExpect(jsonPath("$[0].currentUnit").doesNotExist())
            .andExpect(jsonPath("$[0].totalIncoming").doesNotExist())
            .andExpect(jsonPath("$[0].totalBenefit").doesNotExist())
            .andExpect(jsonPath("$[0].buyTransactionDate").doesNotExist())
    }

    @Test
    fun `getProfitStockLotLoss should filter by ownerId when provided`() {
        val date = LocalDate.of(2025, 1, 1)
        val profitlossList = listOf(
            ProfitlossStockLotDto(
                stockCode = "1234",
                stockName = "Toyota",
                minimal_unit = 1,
                purchasePrice = 1200.25,
                currentPrice = 1300.0,
                currentUnit = 100,
                totalIncoming = BigDecimal("50000"),
                totalBenefit = BigDecimal("2000"),
                buyTransactionDate = date
            )
        )

        whenever(profitlossService.getProfitStockLotLoss(1)).thenReturn(profitlossList)

        mockMvc.perform(get("/api/profitloss").param("ownerId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockCode").value("1234"))
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[0].minimal_unit").value(1))
            .andExpect(jsonPath("$[0].purchasePrice").value(1200.25))
            .andExpect(jsonPath("$[0].currentPrice").value(1300.0))
            .andExpect(jsonPath("$[0].totalIncoming").value(50000))
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    fun `getProfitStockLotLoss should return empty list when no stock lots exist for specific owner`() {
        whenever(profitlossService.getProfitStockLotLoss(1)).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss").param("ownerId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }
}
