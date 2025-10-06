package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class IncomingHistoryModelTest {

    /* SellTransactionがNullパターン */
    @Test
    fun testIncomingHistoryCreation() {
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
        val buyTransactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            stock_lot = stockLot,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            is_nisa = true,
            transaction_date = buyTransactionDate
        )
        val PaymentDate = LocalDate.of(2023, 3, 1)
        val incomingHistory = IncomingHistory(
            id = 1,
            stock_lot = stockLot,
            sell_transaction = null,
            incoming = BigDecimal("200.25"),
            payment_date = PaymentDate
        )

        assertEquals(1, incomingHistory.id)
        assertEquals(stockLot, incomingHistory.stock_lot)
        assertEquals(BigDecimal("200.25"), incomingHistory.incoming)
        assertEquals(null, incomingHistory.sell_transaction)
        assertEquals(PaymentDate, incomingHistory.payment_date)
    }

    /* StockLotIdがNullパターン */
    @Test
    fun testIncomingHistoryCreationWithNullStockLotId() {
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
        val buyTransactionDate = LocalDate.of(2023, 1, 1)
        val buyTransaction = BuyTransaction(
            id = 1,
            stock_lot = stockLot,
            unit = 100,
            price = BigDecimal("1000.50"),
            fee = BigDecimal("10.50"),
            is_nisa = true,
            transaction_date = buyTransactionDate
        )
        val sellTransactionDate = LocalDate.of(2023, 2, 1)
        val sellTransaction = SellTransaction(
            id = 1,
            buy_transaction = buyTransaction,
            unit = 100,
            price = BigDecimal("1200.75"),
            fee = BigDecimal("12.25"),
            transaction_date = sellTransactionDate
        )
        val PaymentDate = LocalDate.of(2023, 3, 1)
        val incomingHistory = IncomingHistory(
            id = 1,
            stock_lot = null,
            sell_transaction = sellTransaction,
            incoming = BigDecimal("200.25"),
            payment_date = PaymentDate
        )

        assertEquals(1, incomingHistory.id)
        assertEquals(null, incomingHistory.stock_lot)
        assertEquals(sellTransaction, incomingHistory.sell_transaction)
        assertEquals(BigDecimal("200.25"), incomingHistory.incoming)
        assertEquals(PaymentDate, incomingHistory.payment_date)
    }
}
