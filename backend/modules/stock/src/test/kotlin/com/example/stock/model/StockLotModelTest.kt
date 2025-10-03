package com.example.stock.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate

class StockLotTest {

    @Test
    fun testStockLotCreation() {
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
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, unit = 100, isNisa = true)

        assertEquals(1, stockLot.id)
        assertEquals(owner, stockLot.owner)
        assertEquals(stock, stockLot.stock)
        assertEquals(100, stockLot.unit)
        assertEquals(true, stockLot.isNisa)
    }
}
