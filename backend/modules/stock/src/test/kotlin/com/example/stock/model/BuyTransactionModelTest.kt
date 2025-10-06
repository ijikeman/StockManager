package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class BuyTransactionModelTest {

    @Test
    fun testBuyTransactionCreation() {
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

        assertEquals(1, buyTransaction.id)
        assertEquals(stockLot, buyTransaction.stockLot)
        assertEquals(100, buyTransaction.unit)
        assertEquals(BigDecimal("1000.50"), buyTransaction.price)
        assertEquals(BigDecimal("10.50"), buyTransaction.fee)
        assertEquals(true, buyTransaction.isNisa)
        assertEquals(buyTransactionDate, buyTransaction.transactionDate)
    }
}
