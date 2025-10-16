package com.example.stock.service

import com.example.stock.dto.StockLotSellDto
import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Sector
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.StockLotRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Integration test for selling stock lots down to 0 units.
 * This test verifies that stock lots can be sold completely (currentUnit = 0)
 * without being deleted, preserving historical transaction data.
 */
@DataJpaTest
@Import(StockLotService::class, SellTransactionService::class, BuyTransactionService::class)
class SellStockLotIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var stockLotService: StockLotService

    @Autowired
    private lateinit var stockLotRepository: StockLotRepository

    @Autowired
    private lateinit var buyTransactionRepository: BuyTransactionRepository

    @Test
    fun `sellStockLot should allow selling all units down to 0`() {
        // Arrange: Create test data
        val sector = entityManager.persist(Sector(name = "Test Sector"))
        val owner = entityManager.persist(Owner(name = "TestOwner"))
        val stock = entityManager.persist(Stock(
            code = "TEST",
            name = "Test Stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = LocalDate.of(2025, 1, 1),
            sector = sector
        ))

        val stockLot = entityManager.persist(StockLot(
            owner = owner,
            stock = stock,
            currentUnit = 5
        ))

        val buyTransaction = entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 5,
            price = BigDecimal("1000.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 1)
        ))

        entityManager.flush()
        entityManager.clear()

        // Act: Sell all 5 units
        val sellDto = StockLotSellDto(
            unit = 5,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("15.00"),
            transactionDate = LocalDate.of(2025, 10, 16)
        )

        stockLotService.sellStockLot(stockLot.id, sellDto)

        // Assert: Stock lot should still exist with currentUnit = 0
        val updatedLot = stockLotRepository.findById(stockLot.id)
        assertThat(updatedLot).isPresent
        assertThat(updatedLot.get().currentUnit).isEqualTo(0)

        // Assert: Stock lot should not appear in queries for active lots (currentUnit > 0)
        val activeLots = stockLotRepository.findAllByCurrentUnitGreaterThan(0)
        assertThat(activeLots).doesNotContain(updatedLot.get())

        // Assert: Buy transaction should still exist
        val transactions = buyTransactionRepository.findByStockLotId(stockLot.id)
        assertThat(transactions).hasSize(1)
    }

    @Test
    fun `sellStockLot should allow partial sales and then complete sale`() {
        // Arrange: Create test data
        val sector = entityManager.persist(Sector(name = "Test Sector"))
        val owner = entityManager.persist(Owner(name = "TestOwner"))
        val stock = entityManager.persist(Stock(
            code = "TEST2",
            name = "Test Stock 2",
            currentPrice = 1500.0,
            incoming = 20.0,
            earningsDate = LocalDate.of(2025, 1, 1),
            sector = sector
        ))

        val stockLot = entityManager.persist(StockLot(
            owner = owner,
            stock = stock,
            currentUnit = 10
        ))

        entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 10,
            price = BigDecimal("1500.00"),
            fee = BigDecimal("20.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 1)
        ))

        entityManager.flush()
        entityManager.clear()

        // Act: First sell 7 units
        stockLotService.sellStockLot(stockLot.id, StockLotSellDto(
            unit = 7,
            price = BigDecimal("1600.00"),
            fee = BigDecimal("10.00"),
            transactionDate = LocalDate.of(2025, 10, 10)
        ))

        // Assert: Should have 3 units remaining
        val afterFirstSale = stockLotRepository.findById(stockLot.id)
        assertThat(afterFirstSale.get().currentUnit).isEqualTo(3)

        // Act: Then sell remaining 3 units
        stockLotService.sellStockLot(stockLot.id, StockLotSellDto(
            unit = 3,
            price = BigDecimal("1700.00"),
            fee = BigDecimal("10.00"),
            transactionDate = LocalDate.of(2025, 10, 15)
        ))

        // Assert: Should have 0 units remaining but still exist
        val afterSecondSale = stockLotRepository.findById(stockLot.id)
        assertThat(afterSecondSale).isPresent
        assertThat(afterSecondSale.get().currentUnit).isEqualTo(0)
    }
}
