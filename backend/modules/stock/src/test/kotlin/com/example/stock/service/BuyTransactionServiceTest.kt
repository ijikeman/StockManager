package com.example.stock.service

import com.example.stock.model.BuyTransaction
import com.example.stock.model.StockLot
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.repository.BuyTransactionRepository
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
class BuyTransactionServiceTest {

    @InjectMocks
    private lateinit var buyTransactionService: BuyTransactionService

    @Mock
    private lateinit var buyTransactionRepository: BuyTransactionRepository

    @Captor
    private lateinit var buyTransactionCaptor: ArgumentCaptor<BuyTransaction>

    @Test
    fun `create should save and return BuyTransaction`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 100)
        val buyTransaction = BuyTransaction(
            id = 0,
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1000.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 6)
        )
        val savedTransaction = buyTransaction.copy(id = 123)
        mockitoWhen(buyTransactionRepository.save(any(BuyTransaction::class.java))).thenReturn(savedTransaction)

        // Act
        val result = buyTransactionService.create(buyTransaction)

        // Assert
        verify(buyTransactionRepository).save(buyTransactionCaptor.capture())
        val captured = buyTransactionCaptor.value
        assertThat(result).isEqualTo(savedTransaction)
        assertThat(captured).isEqualTo(buyTransaction)
    }
}
