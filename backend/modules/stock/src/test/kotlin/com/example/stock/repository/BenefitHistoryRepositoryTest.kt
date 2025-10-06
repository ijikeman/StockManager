package com.example.stock.repository

import com.example.stock.model.Owner
import com.example.stock.model.Sector
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
import com.example.stock.model.SellTransaction
import com.example.stock.model.BenefitHistory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.math.BigDecimal

@DataJpaTest
class BenefitHistoryRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var benefitHistoryRepository: BenefitHistoryRepository

    @Test
    fun `save and find BenefitHistory`() {
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)

        val stock = Stock(
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = persistedSector)
        val persistedStock = entityManager.persist(stock)

        val owner = Owner(name = "testuser")
        val persistedOwner = entityManager.persist(owner)
    
        val stockLot = StockLot(
            owner = persistedOwner,
            stock = persistedStock,
            currentUnit = 100,
        )
        val persistedStockLot = entityManager.persist(stockLot)

        val benefitHistory = BenefitHistory(
            stockLot = persistedStockLot,
            sellTransaction = null,
            benefit = BigDecimal("400.0"),
            paymentDate = java.time.LocalDate.now()
        )
        val persistedBenefitHistory = entityManager.persist(benefitHistory)

        val found = benefitHistoryRepository.findById(persistedBenefitHistory.id)
        assertThat(found).isPresent
        assertEquals(found.get().stockLot, benefitHistory.stockLot)
        assertEquals(null, benefitHistory.sellTransaction)
        assertEquals(found.get().benefit, benefitHistory.benefit)
        assertEquals(found.get().paymentDate, benefitHistory.paymentDate)
    }

    @Test
    fun `save and find BenefitHistory with SellTransaction`() {
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)

        val stock = Stock(
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = persistedSector)
        val persistedStock = entityManager.persist(stock)

        val owner = Owner(name = "testuser")
        val persistedOwner = entityManager.persist(owner)
    
        val stockLot = StockLot(
            owner = persistedOwner,
            stock = persistedStock,
            currentUnit = 100,
        )
        val persistedStockLot = entityManager.persist(stockLot)

        val buyTransaction = BuyTransaction(
            stockLot = persistedStockLot,
            unit = 10,
            price = BigDecimal("1000.0"),
            fee = BigDecimal("100.0"),
            isNisa = true,
            transactionDate = java.time.LocalDate.now()
        )
        val persistedBuyTransaction = entityManager.persist(buyTransaction)

        val sellTransaction = SellTransaction(
            buyTransaction = persistedBuyTransaction,
            unit = 10,
            price = BigDecimal("500.0"),
            fee = BigDecimal("100.0"),
            transactionDate = java.time.LocalDate.now()
        )
        val persistedSellTransaction = entityManager.persist(sellTransaction)

        val benefitHistory = BenefitHistory(
            stockLot = null,
            sellTransaction = persistedSellTransaction,
            benefit = BigDecimal("400.0"),
            paymentDate = java.time.LocalDate.now()
        )
        val persistedBenefitHistory = entityManager.persist(benefitHistory)

        val found = benefitHistoryRepository.findById(persistedBenefitHistory.id)
        assertThat(found).isPresent
        assertEquals(null, benefitHistory.stockLot)
        assertEquals(found.get().sellTransaction, benefitHistory.sellTransaction)
        assertEquals(found.get().benefit, benefitHistory.benefit)
        assertEquals(found.get().paymentDate, benefitHistory.paymentDate)
    }
}
