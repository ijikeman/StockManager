package com.example.stock.service

import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.IncomingHistory
import com.example.stock.model.BenefitHistory
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.BenefitHistoryRepository
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

    @Mock
    private lateinit var incomingHistoryRepository: IncomingHistoryRepository

    @Mock
    private lateinit var benefitHistoryRepository: BenefitHistoryRepository

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

        val incomingHistory1 = IncomingHistory(
            id = 1,
            stockLot = stockLots[0],
            incoming = BigDecimal("100.50"),
            paymentDate = LocalDate.now()
        )
        val incomingHistory2 = IncomingHistory(
            id = 2,
            stockLot = stockLots[0],
            incoming = BigDecimal("50.25"),
            paymentDate = LocalDate.now()
        )
        val incomingHistory3 = IncomingHistory(
            id = 3,
            stockLot = stockLots[1],
            incoming = BigDecimal("200.00"),
            paymentDate = LocalDate.now()
        )

        val benefitHistory1 = BenefitHistory(
            id = 1,
            stockLot = stockLots[0],
            benefit = BigDecimal("1000.00"),
            paymentDate = LocalDate.now()
        )
        val benefitHistory2 = BenefitHistory(
            id = 2,
            stockLot = stockLots[1],
            benefit = BigDecimal("500.00"),
            paymentDate = LocalDate.now()
        )

        mockitoWhen(stockLotService.findAll()).thenReturn(stockLots)
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction1))
        mockitoWhen(buyTransactionRepository.findByStockLotId(2)).thenReturn(listOf(buyTransaction2))
        mockitoWhen(incomingHistoryRepository.findByStockLotId(1)).thenReturn(listOf(incomingHistory1, incomingHistory2))
        mockitoWhen(incomingHistoryRepository.findByStockLotId(2)).thenReturn(listOf(incomingHistory3))
        mockitoWhen(benefitHistoryRepository.findByStockLotId(1)).thenReturn(listOf(benefitHistory1))
        mockitoWhen(benefitHistoryRepository.findByStockLotId(2)).thenReturn(listOf(benefitHistory2))

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
        assertThat(result[0].totalDividend).isEqualTo(150.75) // 100.50 + 50.25
        assertThat(result[0].totalBenefit).isEqualTo(1000.00)
        assertThat(result[1].stockCode).isEqualTo("5678")
        assertThat(result[1].stockName).isEqualTo("Sony")
        assertThat(result[1].purchasePrice).isEqualTo(2100.75)
        assertThat(result[1].totalDividend).isEqualTo(200.00)
        assertThat(result[1].totalBenefit).isEqualTo(500.00)
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
        mockitoWhen(incomingHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())
        mockitoWhen(benefitHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(0.0)
        assertThat(result[0].totalDividend).isEqualTo(0.0)
        assertThat(result[0].totalBenefit).isEqualTo(0.0)
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
        mockitoWhen(incomingHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())
        mockitoWhen(benefitHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())

        // when
        val result = profitlossService.getProfitLoss(1)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
        assertThat(result[0].totalDividend).isEqualTo(0.0)
        assertThat(result[0].totalBenefit).isEqualTo(0.0)
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
        mockitoWhen(incomingHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())
        mockitoWhen(benefitHistoryRepository.findByStockLotId(1)).thenReturn(emptyList())
        // Note: We don't stub stockLotId 2 because it's filtered out due to zero currentUnit

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(1) // Only the stock lot with non-zero units should be returned
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.25)
        assertThat(result[0].totalDividend).isEqualTo(0.0)
        assertThat(result[0].totalBenefit).isEqualTo(0.0)
    }

    @Test
    fun `getProfitLoss should calculate total dividends and benefits correctly with multiple records`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Toyota", currentPrice = 1500.0, minimalUnit = 100)
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, currentUnit = 10)

        val buyTransaction = BuyTransaction(
            id = 1,
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("0"),
            isNisa = false,
            transactionDate = LocalDate.now()
        )

        // Multiple dividend payments
        val incomingHistory1 = IncomingHistory(
            id = 1,
            stockLot = stockLot,
            incoming = BigDecimal("100.00"),
            paymentDate = LocalDate.now().minusMonths(3)
        )
        val incomingHistory2 = IncomingHistory(
            id = 2,
            stockLot = stockLot,
            incoming = BigDecimal("150.50"),
            paymentDate = LocalDate.now().minusMonths(2)
        )
        val incomingHistory3 = IncomingHistory(
            id = 3,
            stockLot = stockLot,
            incoming = BigDecimal("200.75"),
            paymentDate = LocalDate.now().minusMonths(1)
        )

        // Multiple benefit payments
        val benefitHistory1 = BenefitHistory(
            id = 1,
            stockLot = stockLot,
            benefit = BigDecimal("500.00"),
            paymentDate = LocalDate.now().minusMonths(6)
        )
        val benefitHistory2 = BenefitHistory(
            id = 2,
            stockLot = stockLot,
            benefit = BigDecimal("1000.25"),
            paymentDate = LocalDate.now().minusMonths(3)
        )

        mockitoWhen(stockLotService.findAll()).thenReturn(listOf(stockLot))
        mockitoWhen(buyTransactionRepository.findByStockLotId(1)).thenReturn(listOf(buyTransaction))
        mockitoWhen(incomingHistoryRepository.findByStockLotId(1)).thenReturn(
            listOf(incomingHistory1, incomingHistory2, incomingHistory3)
        )
        mockitoWhen(benefitHistoryRepository.findByStockLotId(1)).thenReturn(
            listOf(benefitHistory1, benefitHistory2)
        )

        // when
        val result = profitlossService.getProfitLoss()

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].stockCode).isEqualTo("1234")
        assertThat(result[0].stockName).isEqualTo("Toyota")
        assertThat(result[0].purchasePrice).isEqualTo(1200.00)
        assertThat(result[0].totalDividend).isEqualTo(451.25) // 100.00 + 150.50 + 200.75
        assertThat(result[0].totalBenefit).isEqualTo(1500.25) // 500.00 + 1000.25
    }
}
