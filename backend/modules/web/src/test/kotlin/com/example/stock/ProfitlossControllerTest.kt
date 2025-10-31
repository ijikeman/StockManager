package com.example.stock

import com.example.stock.dto.ProfitlossDto
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

@WebMvcTest(ProfitlossController::class)
class ProfitlossControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var profitlossService: ProfitlossService

    @Test
    fun `getProfitLoss should return list of stock names and buy prices`() {
        val profitlossList = listOf(
            ProfitlossDto(
                stockCode = "1234",
                stockName = "Toyota",
                purchasePrice = 1200.25
            ),
            ProfitlossDto(
                stockCode = "5678",
                stockName = "Sony",
                purchasePrice = 2100.75
            )
        )

        whenever(profitlossService.getProfitLoss()).thenReturn(profitlossList)

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[1].stockName").value("Sony"))
            .andExpect(jsonPath("$[0].purchasePrice").value(1200.25))
            .andExpect(jsonPath("$[1].purchasePrice").value(2100.75))
    }

    @Test
    fun `getProfitLoss should return empty list when no stock lots exist`() {
        whenever(profitlossService.getProfitLoss()).thenReturn(emptyList())

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `getProfitLoss should handle stock lots without buy transactions`() {
        val profitlossList = listOf(
            ProfitlossDto(
                stockCode = "1234",
                stockName = "Toyota",
                purchasePrice = 0.0
            )
        )

        whenever(profitlossService.getProfitLoss()).thenReturn(profitlossList)

        mockMvc.perform(get("/api/profitloss"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].stockName").value("Toyota"))
            .andExpect(jsonPath("$[0].purchasePrice").value(0.0))
    }
}
