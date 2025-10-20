package com.example.stock.service

import com.example.stock.dto.IncomingHistoryAddDto
import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.model.StockLot
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.repository.StockLotRepository
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
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class IncomingHistoryServiceTest {

    @InjectMocks
    private lateinit var incomingHistoryService: IncomingHistoryService

    @Mock
    private lateinit var incomingHistoryRepository: IncomingHistoryRepository

    @Mock
    private lateinit var stockLotRepository: StockLotRepository

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

    @Test
    fun `create from DTO should save and return IncomingHistory`() {
        // Arrange
        val dto = IncomingHistoryAddDto(
            paymentDate = LocalDate.of(2025, 10, 7),
            lotId = 1,
            incoming = BigDecimal("1000.00")
        )

        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 100)
        mockitoWhen(stockLotRepository.findById(dto.lotId)).thenReturn(Optional.of(stockLot))

        val expectedHistory = IncomingHistory(
            stockLot = stockLot,
            incoming = dto.incoming,
            paymentDate = dto.paymentDate
        )
        val savedHistory = expectedHistory.copy(id = 124)
        mockitoWhen(incomingHistoryRepository.save(any(IncomingHistory::class.java))).thenReturn(savedHistory)

        // Act
        val result = incomingHistoryService.create(dto)

        // Assert
        verify(incomingHistoryRepository).save(incomingHistoryCaptor.capture())
        val captured = incomingHistoryCaptor.value

        assertThat(result).isEqualTo(savedHistory)
        assertThat(captured.stockLot).isEqualTo(stockLot)
        assertThat(captured.incoming).isEqualByComparingTo(dto.incoming)
        assertThat(captured.paymentDate).isEqualTo(dto.paymentDate)
    }

    @Test
    fun `update should update and return IncomingHistory`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 100)
        
        val existing = IncomingHistory(
            id = 123,
            stockLot = stockLot,
            sellTransaction = null,
            incoming = BigDecimal("500.00"),
            paymentDate = LocalDate.of(2025, 10, 6)
        )
        
        val updateDto = IncomingHistoryAddDto(
            paymentDate = LocalDate.of(2025, 10, 8),
            lotId = 1,
            incoming = BigDecimal("600.00")
        )
        
        val updatedHistory = existing.copy(
            paymentDate = updateDto.paymentDate,
            incoming = updateDto.incoming
        )
        
        mockitoWhen(incomingHistoryRepository.findById(123)).thenReturn(Optional.of(existing))
        mockitoWhen(incomingHistoryRepository.save(any(IncomingHistory::class.java))).thenReturn(updatedHistory)
        
        // Act
        val result = incomingHistoryService.update(123, updateDto)
        
        // Assert
        verify(incomingHistoryRepository).save(incomingHistoryCaptor.capture())
        val captured = incomingHistoryCaptor.value
        
        assertThat(result).isEqualTo(updatedHistory)
        assertThat(captured.paymentDate).isEqualTo(updateDto.paymentDate)
        assertThat(captured.incoming).isEqualByComparingTo(updateDto.incoming)
        assertThat(captured.stockLot).isEqualTo(stockLot)
    }
}
