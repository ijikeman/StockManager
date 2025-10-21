package com.example.stock.service

import com.example.stock.dto.StockLotSellDto
import com.example.stock.model.*
import com.example.stock.repository.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.Import
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Integration test for selling stock lots with IncomingHistory and BenefitHistory duplication.
 * This test verifies that when selling a stocklot, IncomingHistory and BenefitHistory records
 * are duplicated with stockLotId set to null and sellTransactionId set to the created sell transaction.
 */
@DataJpaTest
@Import(StockLotService::class, SellTransactionService::class, BuyTransactionService::class)
class StockLotSellWithHistoryIntegrationTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var stockLotService: StockLotService

    @Autowired
    private lateinit var stockLotRepository: StockLotRepository

    @Autowired
    private lateinit var incomingHistoryRepository: IncomingHistoryRepository

    @Autowired
    private lateinit var benefitHistoryRepository: BenefitHistoryRepository

    @Autowired
    private lateinit var sellTransactionRepository: SellTransactionRepository

    @Test
    fun `sellStockLot should duplicate IncomingHistory records and link to sell transaction`() {
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

        // Create IncomingHistory records linked to the stockLot
        val incomingHistory1 = entityManager.persist(IncomingHistory(
            stockLot = stockLot,
            sellTransaction = null,
            incoming = BigDecimal("50.00"),
            paymentDate = LocalDate.of(2025, 9, 1)
        ))

        val incomingHistory2 = entityManager.persist(IncomingHistory(
            stockLot = stockLot,
            sellTransaction = null,
            incoming = BigDecimal("60.00"),
            paymentDate = LocalDate.of(2025, 10, 1)
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

        // Assert: Original IncomingHistory records should still exist with stockLotId
        val originalHistories = incomingHistoryRepository.findByStockLotId(stockLot.id)
        assertThat(originalHistories).hasSize(2)
        assertThat(originalHistories.map { it.id }).containsExactlyInAnyOrder(incomingHistory1.id, incomingHistory2.id)

        // Assert: New SellTransaction should be created
        val sellTransactions = sellTransactionRepository.findByBuyTransactionId(buyTransaction.id)
        assertThat(sellTransactions).hasSize(1)
        val sellTransaction = sellTransactions[0]

        // Assert: Duplicated IncomingHistory records should be created linked to sellTransaction
        val duplicatedHistories = incomingHistoryRepository.findBySellTransactionId(sellTransaction.id)
        assertThat(duplicatedHistories).hasSize(2)
        
        // Verify that duplicated records have no stockLot but have sellTransaction
        duplicatedHistories.forEach { history ->
            assertThat(history.stockLot).isNull()
            assertThat(history.sellTransaction).isNotNull
            assertThat(history.sellTransaction?.id).isEqualTo(sellTransaction.id)
        }
        
        // Verify that the duplicated records have the same incoming amounts
        val duplicatedIncomings = duplicatedHistories.map { it.incoming }.toSet()
        assertThat(duplicatedIncomings).containsExactlyInAnyOrder(BigDecimal("50.00"), BigDecimal("60.00"))

        // Verify that the duplicated records have the same payment dates
        val duplicatedPaymentDates = duplicatedHistories.map { it.paymentDate }.toSet()
        assertThat(duplicatedPaymentDates).containsExactlyInAnyOrder(LocalDate.of(2025, 9, 1), LocalDate.of(2025, 10, 1))
    }

    @Test
    fun `sellStockLot should duplicate BenefitHistory records and link to sell transaction`() {
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
            currentUnit = 3
        ))

        val buyTransaction = entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 3,
            price = BigDecimal("1500.00"),
            fee = BigDecimal("20.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 9, 1)
        ))

        // Create BenefitHistory records linked to the stockLot
        val benefitHistory1 = entityManager.persist(BenefitHistory(
            stockLot = stockLot,
            sellTransaction = null,
            benefit = BigDecimal("100.00"),
            paymentDate = LocalDate.of(2025, 9, 15)
        ))

        entityManager.flush()
        entityManager.clear()

        // Act: Sell all 3 units
        val sellDto = StockLotSellDto(
            unit = 3,
            price = BigDecimal("1600.00"),
            fee = BigDecimal("10.00"),
            transactionDate = LocalDate.of(2025, 10, 20)
        )

        stockLotService.sellStockLot(stockLot.id, sellDto)

        // Assert: Original BenefitHistory record should still exist with stockLotId
        val originalHistories = benefitHistoryRepository.findByStockLotId(stockLot.id)
        assertThat(originalHistories).hasSize(1)
        assertThat(originalHistories[0].id).isEqualTo(benefitHistory1.id)

        // Assert: New SellTransaction should be created
        val sellTransactions = sellTransactionRepository.findByBuyTransactionId(buyTransaction.id)
        assertThat(sellTransactions).hasSize(1)
        val sellTransaction = sellTransactions[0]

        // Assert: Duplicated BenefitHistory record should be created linked to sellTransaction
        val duplicatedHistories = benefitHistoryRepository.findBySellTransactionId(sellTransaction.id)
        assertThat(duplicatedHistories).hasSize(1)
        
        val duplicatedHistory = duplicatedHistories[0]
        assertThat(duplicatedHistory.stockLot).isNull()
        assertThat(duplicatedHistory.sellTransaction).isNotNull
        assertThat(duplicatedHistory.sellTransaction?.id).isEqualTo(sellTransaction.id)
        assertThat(duplicatedHistory.benefit).isEqualTo(BigDecimal("100.00"))
        assertThat(duplicatedHistory.paymentDate).isEqualTo(LocalDate.of(2025, 9, 15))
    }

    @Test
    fun `sellStockLot should create multiple duplicates when selling creates multiple sell transactions`() {
        // Arrange: Create test data with multiple buy transactions
        val sector = entityManager.persist(Sector(name = "Test Sector"))
        val owner = entityManager.persist(Owner(name = "TestOwner"))
        val stock = entityManager.persist(Stock(
            code = "TEST3",
            name = "Test Stock 3",
            currentPrice = 2000.0,
            incoming = 30.0,
            earningsDate = LocalDate.of(2025, 1, 1),
            sector = sector
        ))

        val stockLot = entityManager.persist(StockLot(
            owner = owner,
            stock = stock,
            currentUnit = 10
        ))

        // First buy transaction: 4 units
        val buyTransaction1 = entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 4,
            price = BigDecimal("1800.00"),
            fee = BigDecimal("10.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 8, 1)
        ))

        // Second buy transaction: 6 units
        val buyTransaction2 = entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 6,
            price = BigDecimal("1900.00"),
            fee = BigDecimal("15.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 9, 1)
        ))

        // Create IncomingHistory record linked to the stockLot
        val incomingHistory = entityManager.persist(IncomingHistory(
            stockLot = stockLot,
            sellTransaction = null,
            incoming = BigDecimal("80.00"),
            paymentDate = LocalDate.of(2025, 9, 10)
        ))

        entityManager.flush()
        entityManager.clear()

        // Act: Sell 8 units (will span both buy transactions)
        val sellDto = StockLotSellDto(
            unit = 8,
            price = BigDecimal("2100.00"),
            fee = BigDecimal("20.00"),
            transactionDate = LocalDate.of(2025, 10, 25)
        )

        stockLotService.sellStockLot(stockLot.id, sellDto)

        // Assert: Two sell transactions should be created
        val sellTx1 = sellTransactionRepository.findByBuyTransactionId(buyTransaction1.id)
        val sellTx2 = sellTransactionRepository.findByBuyTransactionId(buyTransaction2.id)
        assertThat(sellTx1).hasSize(1)
        assertThat(sellTx2).hasSize(1)
        
        // Assert: Duplicated IncomingHistory records should be created for each sell transaction
        val duplicatedForSell1 = incomingHistoryRepository.findBySellTransactionId(sellTx1[0].id)
        val duplicatedForSell2 = incomingHistoryRepository.findBySellTransactionId(sellTx2[0].id)
        
        assertThat(duplicatedForSell1).hasSize(1)
        assertThat(duplicatedForSell2).hasSize(1)
        
        // Both duplicates should have the same incoming amount from the original
        assertThat(duplicatedForSell1[0].incoming).isEqualTo(BigDecimal("80.00"))
        assertThat(duplicatedForSell2[0].incoming).isEqualTo(BigDecimal("80.00"))
        
        // Original IncomingHistory should still exist
        val originalHistories = incomingHistoryRepository.findByStockLotId(stockLot.id)
        assertThat(originalHistories).hasSize(1)
        assertThat(originalHistories[0].id).isEqualTo(incomingHistory.id)
    }

    @Test
    fun `sellStockLot should handle case with no IncomingHistory or BenefitHistory`() {
        // Arrange: Create test data without any history records
        val sector = entityManager.persist(Sector(name = "Test Sector"))
        val owner = entityManager.persist(Owner(name = "TestOwner"))
        val stock = entityManager.persist(Stock(
            code = "TEST4",
            name = "Test Stock 4",
            currentPrice = 1200.0,
            incoming = 15.0,
            earningsDate = LocalDate.of(2025, 1, 1),
            sector = sector
        ))

        val stockLot = entityManager.persist(StockLot(
            owner = owner,
            stock = stock,
            currentUnit = 2
        ))

        val buyTransaction = entityManager.persist(BuyTransaction(
            stockLot = stockLot,
            unit = 2,
            price = BigDecimal("1200.00"),
            fee = BigDecimal("5.00"),
            isNisa = false,
            transactionDate = LocalDate.of(2025, 10, 1)
        ))

        entityManager.flush()
        entityManager.clear()

        // Act: Sell all units
        val sellDto = StockLotSellDto(
            unit = 2,
            price = BigDecimal("1300.00"),
            fee = BigDecimal("8.00"),
            transactionDate = LocalDate.of(2025, 10, 30)
        )

        stockLotService.sellStockLot(stockLot.id, sellDto)

        // Assert: Sell transaction should be created successfully
        val sellTransactions = sellTransactionRepository.findByBuyTransactionId(buyTransaction.id)
        assertThat(sellTransactions).hasSize(1)
        
        // Assert: No duplicated history records should be created
        val duplicatedIncoming = incomingHistoryRepository.findBySellTransactionId(sellTransactions[0].id)
        val duplicatedBenefit = benefitHistoryRepository.findBySellTransactionId(sellTransactions[0].id)
        
        assertThat(duplicatedIncoming).isEmpty()
        assertThat(duplicatedBenefit).isEmpty()
        
        // Assert: Stock lot should have reduced units
        val updatedLot = stockLotRepository.findById(stockLot.id)
        assertThat(updatedLot).isPresent
        assertThat(updatedLot.get().currentUnit).isEqualTo(0)
    }
}
