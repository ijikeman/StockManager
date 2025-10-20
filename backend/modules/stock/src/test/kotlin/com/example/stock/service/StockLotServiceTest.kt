package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Sector
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.SellTransactionRepository
import com.example.stock.service.BuyTransactionService
import com.example.stock.service.SellTransactionService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import java.math.BigDecimal
import java.time.LocalDate
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
    private lateinit var buyTransactionRepository: BuyTransactionRepository

    @Mock
    private lateinit var sellTransactionRepository: SellTransactionRepository

    @Mock
    private lateinit var buyTransactionService: BuyTransactionService

    @Mock
    private lateinit var sellTransactionService: SellTransactionService

    @Captor
    private lateinit var stockLotCaptor: ArgumentCaptor<StockLot>

    @Test
    fun `createStockLot should create a single lot with correct unit and buy transaction`() {
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
        val price = java.math.BigDecimal("1000.0")
        val fee = java.math.BigDecimal("10.0")
        val isNisa = false
        val transactionDate = java.time.LocalDate.now()

        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }
        mockitoWhen(buyTransactionRepository.save(any(com.example.stock.model.BuyTransaction::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = stockLotService.createStockLot(
            owner = owner,
            stock = stock,
            unit = currentUnit,
            price = price,
            fee = fee,
            isNisa = isNisa,
            transactionDate = transactionDate
        )

        // then
        verify(stockLotRepository).save(stockLotCaptor.capture())
        val capturedStockLot = stockLotCaptor.value

        assertThat(result).isNotNull
        assertThat(capturedStockLot.currentUnit).isEqualTo(currentUnit)
        assertThat(capturedStockLot.owner).isEqualTo(owner)
        assertThat(capturedStockLot.stock).isEqualTo(stock)

        // BuyTransactionの検証
        val buyTransactionCaptor = ArgumentCaptor.forClass(com.example.stock.model.BuyTransaction::class.java)
        verify(buyTransactionRepository).save(buyTransactionCaptor.capture())
        val capturedBuyTransaction = buyTransactionCaptor.value

        assertThat(capturedBuyTransaction.stockLot).isEqualTo(result)
        assertThat(capturedBuyTransaction.unit).isEqualTo(currentUnit)
        assertThat(capturedBuyTransaction.price).isEqualTo(price)
        assertThat(capturedBuyTransaction.fee).isEqualTo(fee)
        assertThat(capturedBuyTransaction.isNisa).isEqualTo(isNisa)
        assertThat(capturedBuyTransaction.transactionDate).isEqualTo(transactionDate)
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
