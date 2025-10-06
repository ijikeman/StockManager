package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class BenefitHistoryModelTest {

    /* SellTransactionがNullパターン */
    @Test
    fun testBenefitHistoryCreation() {
        val owner = Owner(id = 1, name = "TestOwner")
        val sector = Sector(id = 1, name = "Test Sector")
        val stock = Stock(
            id = 1,
            code = "1234",
            name = "Test Stock",
            sector = sector,
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = LocalDate.now(),
            minimalUnit = 100
        )
        val stockLot = StockLot(
            id = 1,
            owner = owner,
            stock = stock,
            currentUnit = 100
        )
        val buyTransactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            stockLot = stockLot,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            isNisa = true,
            transactionDate = buyTransactionDate
        )
        val paymentDate = LocalDate.of(2023, 3, 1)
        val benefitHistory = BenefitHistory(
            id = 1,
            stockLot = stockLot,
            sellTransaction = null,
            benefit = BigDecimal("200.25"),
            paymentDate = paymentDate
        )

        assertEquals(1, benefitHistory.id)
        assertEquals(stockLot, benefitHistory.stockLot)
        assertEquals(BigDecimal("200.25"), benefitHistory.benefit)
        assertEquals(null, benefitHistory.sellTransaction)
        assertEquals(paymentDate, benefitHistory.paymentDate)
    }

    /* StockLotIdがNullパターン */
    @Test
    fun testBenefitHistoryCreationWithNullStockLotId() {
        val owner = Owner(id = 1, name = "TestOwner")
        val sector = Sector(id = 1, name = "Test Sector")
        val stock = Stock(
            id = 1,
            code = "1234",
            name = "Test Stock",
            sector = sector,
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = LocalDate.now(),
            minimalUnit = 100
        )
        val stockLot = StockLot(
            id = 1,
            owner = owner,
            stock = stock,
            currentUnit = 100
        )
        val buyTransactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            stockLot = stockLot,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            isNisa = true,
            transactionDate = buyTransactionDate
        )
        val sellTransactionDate = LocalDate.of(2023, 2, 1)
        val sellTransaction = SellTransaction(
            id = 1,
            buyTransaction = buyTransaction,
            unit = 100,
            price = BigDecimal("1200.75"),
            fee = BigDecimal("12.25"),
            transactionDate = sellTransactionDate
        )
        val paymentDate = LocalDate.of(2023, 3, 1)
        val benefitHistory = BenefitHistory(
            id = 1,
            stockLot = null,
            sellTransaction = sellTransaction,
            benefit = BigDecimal("200.25"),
            paymentDate = paymentDate
        )

        assertEquals(1, benefitHistory.id)
        assertEquals(null, benefitHistory.stockLot)
        assertEquals(sellTransaction, benefitHistory.sellTransaction)
        assertEquals(BigDecimal("200.25"), benefitHistory.benefit)
        assertEquals(paymentDate, benefitHistory.paymentDate)
    }
}
