package com.example.stock.service

import com.example.stock.model.Holding
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.Transaction
import com.example.stock.model.IncomeHistory
import com.example.stock.provider.FinanceProvider
import com.example.stock.provider.StockInfo
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.TransactionRepository
import com.example.stock.repository.IncomeHistoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ProfitLossServiceTest {

    @InjectMocks
    private lateinit var profitLossService: ProfitLossService

    @Mock
    private lateinit var holdingRepository: HoldingRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var incomeHistoryRepository: IncomeHistoryRepository

    @Mock
    private lateinit var financeProvider: FinanceProvider

    @Test
    fun `calculateTotalProfitLoss should return correct summary`() {
        // Arrange
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", current_price = 1500.0, dividend = 0.0)
        val holding = Holding(id = 1, owner = owner, stock = stock, current_volume = 100, average_price = 1000.0, nisa = false)

        val sellTransaction = Transaction(
            id = 1,
            holding = holding,
            transaction_type = "sell",
            volume = 50,
            price = 1200.0,
            average_price_at_transaction = 1000.0, // Use the holding's average price at the time
            tax = 500.0,
            date = LocalDate.now()
        )

        val dividend = IncomeHistory(
            id = 1,
            holding = holding,
            income_type = "dividend",
            amount = 10000.0,
            date = LocalDate.now()
        )

        val ownerId = 1
        `when`(holdingRepository.findByOwnerId(ownerId)).thenReturn(listOf(holding))
        `when`(transactionRepository.findByHoldingId(holding.id)).thenReturn(listOf(sellTransaction))
        `when`(incomeHistoryRepository.findByHoldingId(holding.id)).thenReturn(listOf(dividend))
        `when`(financeProvider.fetchStockInfo(stock.code)).thenReturn(StockInfo(price = 1500.0, dividend = 0.0, earnings_date = null))

        // Act
        val summary = profitLossService.calculateTotalProfitLoss(ownerId)

        // Assert
        // Realized P/L = (1200 - 1000) * 50 - 500 = 200 * 50 - 500 = 10000 - 500 = 9500
        assertEquals(9500.0, summary.realizedPl)
        // Unrealized P/L = (1500 - 1000) * 100 = 500 * 100 = 50000
        assertEquals(50000.0, summary.unrealizedPl)
        // Dividend Income = 10000
        assertEquals(10000.0, summary.dividendIncome)
        // Total P/L = 9500 + 50000 + 10000 = 69500
        assertEquals(69500.0, summary.totalPl)
    }
}
