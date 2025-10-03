package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class SellTransactionModelTest {

    @Test
    fun testSellTransactionCreation() {
        val owner = Owner(id = 1, name = "TestOwner")
        val sector = Sector(id = 1, name = "Test Sector")
        val stock = Stock(
            id = 1,
            code = "1234",
            name = "Test Stock",
            sector = sector,
            current_price = 1000.0,
            incoming = 10.0,
            earnings_date = LocalDate.now(),
            minimalUnit = 100
        )
        val buyTransactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            owner = owner,
            stock = stock,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            transaction_date = buyTransactionDate
        )

        val sellTransactionDate = LocalDate.of(2023, 2, 1)
        val sellTransaction = SellTransaction(
            id = 1,
            buyTransaction = buyTransaction,
            unit = 100,
            price = BigDecimal("1200.75"),
            fee = BigDecimal("12.25"),
            transaction_date = sellTransactionDate
        )

        assertEquals(1, sellTransaction.id)
        assertEquals(buyTransaction, sellTransaction.buyTransaction)
        assertEquals(100, sellTransaction.unit)
        assertEquals(BigDecimal("1200.75"), sellTransaction.price)
        assertEquals(BigDecimal("12.25"), sellTransaction.fee)
        assertEquals(sellTransactionDate, sellTransaction.transaction_date)
    }
}
