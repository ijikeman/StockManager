package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class StockLotModelTest {

    @Test
    /**
     * StockLotモデルの基本的な作成とプロパティの検証を行います。
     */
    fun testStockLotCreation() {
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
        assertEquals(1, stockLot.id)
        assertEquals(owner, stockLot.owner)
        assertEquals(stock, stockLot.stock)
        assertEquals(100, stockLot.currentUnit)
    }
}
