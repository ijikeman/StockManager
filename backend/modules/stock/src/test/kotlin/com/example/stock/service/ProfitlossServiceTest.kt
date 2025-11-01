package com.example.stock.service

import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.BuyTransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen
import java.math.BigDecimal
import java.time.LocalDate

/**
 * ProfitlossServiceの単体テストクラス。
 */
@ExtendWith(MockitoExtension::class)
class ProfitlossServiceTest {

    @InjectMocks
    private lateinit var profitlossService: ProfitlossService

    @Mock
    private lateinit var stockLotService: StockLotService

    @Mock
    private lateinit var buyTransactionRepository: BuyTransactionRepository

    @Test
    fun `getProfitLoss should return list of profitloss DTOs with purchase prices`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock1 = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stock2 = Stock(id = 2, code = "5678", name = "Sony", currentPrice = 2000.0, minimalUnit = 100)
        val stockLots = listOf(
            StockLot(id = 1, owner = owner, stock = stock1, currentUnit = 10),
            StockLot(id = 2, owner = owner, stock = stock2, currentUnit = 5)
        )

        val buyTransaction1 = BuyTransaction(
            id = 1,
            stockLot = stockLots[0],
            unit = 10,
            price = BigDecimal("1200.25"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )
        val buyTransaction2 = BuyTransaction(
            id = 2,
            stockLot = stockLots[1],
            unit = 5,
            price = BigDecimal("2100.75"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        mockitoWhen(stockLotService.findAll()).thenReturn(stockLots)
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction1))
        mockitoWhen(buyTransactionRepository.findByStockLotId(2)).thenReturn(listOf(buyTransaction2))

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
        assertThat(result[1].stockCode).isEqualTo("5678")
        assertThat(result[1].stockName).isEqualTo("Sony")
        assertThat(result[1].purchasePrice).isEqualTo(2100.75)
    }

    @Test
    fun `getProfitLoss should return empty list when no stock lots exist`() {
        // given
        mockitoWhen(stockLotService.findAll()).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getProfitLoss should handle stock lots without buy transactions`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10)

        mockitoWhen(stockLotService.findAll()).thenReturn(listOf(stockLot))
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(0.0)
    }

    @Test
    fun `getProfitLoss should filter by ownerId when provided`() {
        // given
        val owner1 = Owner(id = 1, name = "Owner 1")
        val stock1 = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stockLotsForOwner1 = listOf(
            StockLot(id = 1, owner = owner1, stock = stock1, currentUnit = 10)
        )

        val buyTransaction1 = BuyTransaction(
            id = 1,
            stockLot = stockLotsForOwner1[0],
            unit = 10,
            price = BigDecimal("1200.25"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        mockitoWhen(stockLotService.findByOwnerId(1)).thenReturn(stockLotsForOwner1)
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction1))

        // when
        val result = profitlossService.getProfitLoss(1)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
    }

    @Test
    fun `getProfitLoss should return empty list when no stock lots exist for specific owner`() {
        // given
        mockitoWhen(stockLotService.findByOwnerId(1)).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss(1)

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `getProfitLoss should exclude stock lots with zero currentUnit`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock1 = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stock2 = Stock(id = 2, code = "5678", name = "Sony", currentPrice = 2000.0, minimalUnit = 100)
        val stockLots = listOf(
            StockLot(id = 1, owner = owner, stock = stock1, currentUnit = 10),
            StockLot(id = 2, owner = owner, stock = stock2, currentUnit = 0) // Zero unit - should be excluded
        )

        val buyTransaction1 = BuyTransaction(
            id = 1,
            stockLot = stockLots[0],
            unit = 10,
            price = BigDecimal("1200.25"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        mockitoWhen(stockLotService.findAll()).thenReturn(stockLots)
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction1))
        mockitoWhen(buyTransactionRepository.findByStockLotId(2)).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(1) // Only the stock lot with non-zero units should be returned
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
    }
}
