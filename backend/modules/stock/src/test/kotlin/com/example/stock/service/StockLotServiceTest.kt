package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Sector
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
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

@ExtendWith(MockitoExtension::class)
class StockLotServiceTest {

    @InjectMocks
    private lateinit var stockLotService: StockLotService

    @Mock
    private lateinit var stockLotRepository: StockLotRepository

    @Mock
    private lateinit var buyTransactionService: BuyTransactionService

    @Mock
    private lateinit var buyTransactionRepository: com.example.stock.repository.BuyTransactionRepository

    @Captor
    private lateinit var stockLotCaptor: ArgumentCaptor<StockLot>

    @Test
    fun `createStockLot should create a single lot with correct unit`() {
        // given
        val owner = Owner(id = 1, name = "TestUser")
        val sector = Sector(id = 1, name = "Test Sector")
        val stock = Stock(
            id = 1,
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = sector
        )
        val currentUnit = 2

        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = stockLotService.createStockLot(owner, stock, currentUnit)

        // then
        verify(stockLotRepository).save(stockLotCaptor.capture())
        val capturedStockLot = stockLotCaptor.value

        assertThat(result).isNotNull
        assertThat(capturedStockLot.currentUnit).isEqualTo(2)
        assertThat(capturedStockLot.owner).isEqualTo(owner)
        assertThat(capturedStockLot.stock).isEqualTo(stock)
    }

    @Test
    fun `createStockLotAndBuyTransaction should create lot and buy transaction`() {
        // given
        val owner = Owner(id = 1, name = "TestUser")
        val sector = Sector(id = 1, name = "Test Sector")
        val stock = Stock(
            id = 1,
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = sector
        )
        val currentUnit = 2
        val dummyStockLot = StockLot(id = 99, owner = owner, stock = stock, currentUnit = currentUnit)
        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenReturn(dummyStockLot)

        val buyTransaction = com.example.stock.model.BuyTransaction(
            id = 0,
            stockLot = dummyStockLot, // 仮の値
            unit = 2,
            price = java.math.BigDecimal("1000.0"),
            fee = java.math.BigDecimal("10.0"),
            isNisa = false,
            transactionDate = java.time.LocalDate.of(2025, 10, 6)
        )
        // buyTransactionService.createのスタブは不要

        // when
        val result = stockLotService.createStockLotAndBuyTransaction(owner, stock, currentUnit, buyTransaction)

        // then
        verify(stockLotRepository).save(any(StockLot::class.java))
        verify(buyTransactionService).create(
            org.mockito.kotlin.argThat { 
                this.copy(id = 0) == buyTransaction.copy(stockLot = dummyStockLot, id = 0)
            }
        )
        assertThat(result).isEqualTo(dummyStockLot)
    }
}
