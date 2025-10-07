package com.example.stock.service

import com.example.stock.model.SellTransaction
import com.example.stock.model.BuyTransaction
import com.example.stock.model.StockLot
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.repository.SellTransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
class SellTransactionServiceTest {

    @InjectMocks
    private lateinit var sellTransactionService: SellTransactionService

    @Mock
    private lateinit var sellTransactionRepository: SellTransactionRepository

    @Mock
    private lateinit var stockLotRepository: com.example.stock.repository.StockLotRepository

    @Captor
    private lateinit var sellTransactionCaptor: ArgumentCaptor<SellTransaction>

    @Test
    fun `create should save and return SellTransaction`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 100)
        val buyTransaction = BuyTransaction(
            id = 10,
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1000.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 6)
        )
        val sellTransaction = SellTransaction(
            id = 0,
            buyTransaction = buyTransaction,
            unit = 5,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("5.00"),
            transactionDate = LocalDate.of(2025, 11, 1)
        )
        val savedTransaction = sellTransaction.copy(id = 123)
        mockitoWhen(sellTransactionRepository.save(any(SellTransaction::class.java))).thenReturn(savedTransaction)

        // Act
        val result = sellTransactionService.create(sellTransaction)

        // Assert
        verify(sellTransactionRepository).save(sellTransactionCaptor.capture())
        val captured = sellTransactionCaptor.value
        assertThat(result).isEqualTo(savedTransaction)
        assertThat(captured).isEqualTo(sellTransaction)
    }

    @Test
    fun `createAndUpdateStockLot should decrease unit and save when enough units`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10)
        val buyTransaction = BuyTransaction(
            id = 10,
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1000.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 6)
        )
        val sellTransaction = SellTransaction(
            id = 0,
            buyTransaction = buyTransaction,
            unit = 4,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("5.00"),
            transactionDate = LocalDate.of(2025, 11, 1)
        )
        val savedTransaction = sellTransaction.copy(id = 124)
        // StockLotRepository.saveはcurrentUnitが減ったStockLotを返す
        org.mockito.Mockito.`when`(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }
        org.mockito.Mockito.`when`(sellTransactionRepository.save(any(SellTransaction::class.java))).thenReturn(savedTransaction)

        // Act
        val result = sellTransactionService.createAndUpdateStockLot(sellTransaction)

        // Assert
        verify(stockLotRepository).save(org.mockito.kotlin.argThat { currentUnit == 6 })
        verify(sellTransactionRepository).save(sellTransactionCaptor.capture())
        assertThat(result).isEqualTo(savedTransaction)
    }

    @Test
    fun `createAndUpdateStockLot should throw if not enough units`() {
        // Arrange
        val owner = Owner(id = 1, name = "testOwner")
        val stock = Stock(id = 1, code = "TST", name = "testStock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 3)
        val buyTransaction = BuyTransaction(
            id = 10,
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1000.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 6)
        )
        val sellTransaction = SellTransaction(
            id = 0,
            buyTransaction = buyTransaction,
            unit = 5,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("5.00"),
            transactionDate = LocalDate.of(2025, 11, 1)
        )

        // Act & Assert
        assertThatThrownBy {
            sellTransactionService.createAndUpdateStockLot(sellTransaction)
        }.isInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("Not enough units")
    }
}
