package com.example.stock.service

import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.model.StockLot
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen
import org.mockito.ArgumentMatchers.any
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class IncomingHistoryServiceTest {

    @InjectMocks
    private lateinit var incomingHistoryService: IncomingHistoryService

    @Mock
    private lateinit var incomingHistoryRepository: IncomingHistoryRepository

    @Captor
    private lateinit var incomingHistoryCaptor: ArgumentCaptor<IncomingHistory>

    @Test
    fun `create should save and return IncomingHistory`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 100)
        val incomingHistory = IncomingHistory(
            id = 0,
            stockLot = stockLot,
            sellTransaction = null,
            incoming = BigDecimal("500.00"),
            paymentDate = LocalDate.of(2025, 10, 6)
        )
        val savedHistory = incomingHistory.copy(id = 123)
        mockitoWhen(incomingHistoryRepository.save(any(IncomingHistory::class.java))).thenReturn(savedHistory)

        // Act
        val result = incomingHistoryService.create(incomingHistory)

        // Assert
        verify(incomingHistoryRepository).save(incomingHistoryCaptor.capture())
        val captured = incomingHistoryCaptor.value
        assertThat(result).isEqualTo(savedHistory)
        assertThat(captured).isEqualTo(incomingHistory)
    }
}
