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
            current_price = 1000.0,
            incoming = 10.0,
            earnings_date = LocalDate.now(),
            minimal_unit = 100
        )
        val stockLot = StockLot(
            id = 1,
            owner = owner,
            stock = stock,
            current_unit = 100
        )
        val transactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            stock_lot = stockLot,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            is_nisa = true,
            transaction_date = transactionDate
        )

        assertEquals(1, buyTransaction.id)
        assertEquals(stockLot, buyTransaction.stock_lot)
        assertEquals(100, buyTransaction.unit)
        assertEquals(BigDecimal("1000.50"), buyTransaction.price)
        assertEquals(BigDecimal("10.50"), buyTransaction.fee)
        assertEquals(true, buyTransaction.is_nisa)
        assertEquals(transactionDate, buyTransaction.transaction_date)
    }
}
