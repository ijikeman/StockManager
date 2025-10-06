package com.example.stock.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StockTest {
    private lateinit var validator: Validator
    private lateinit var sector: Sector

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
        sector = Sector(id = 1, name = "Test Sector")
    }

    // --- 正常系 ---
    @Test
    fun `valid stock should have no violations`() {
        val stock = Stock(
            code = "7203",
            name = "トヨタ自動車",
            currentPrice = 9000.0,
            incoming = 150.0,
            earningsDate = java.time.LocalDate.of(2025, 8, 27),
            sector = sector,
            minimalUnit = 200
        )
        val violations = validator.validate(stock)
        assertEquals(0, violations.size)
    }

    // 必須ではない値は定義しなくても問題がないことを確認
    @Test
    fun `valid stock with optional fields not set should have no violations`() {
        val stock = Stock(
            code = "7203",
            name = "トヨタ自動車",
            sector = sector
        )
        val violations = validator.validate(stock)
        assertEquals(0, violations.size)
    }

    // earningsDate の null 許容
    @Test
    fun `stock with null earningsDate should be valid`() {
        val stock = Stock(code = "1234", name = "Test", earningsDate = null, sector = sector)
        val violations = validator.validate(stock)
        assertEquals(0, violations.size)
    }

    // equals, hashCode, toString
    @Test
    fun `stock equals and hashCode should work`() {
        val stock1 = Stock(code = "1234", name = "Test", sector = sector)
        val stock2 = Stock(code = "1234", name = "Test", sector = sector)
        assertEquals(stock1, stock2)
        assertEquals(stock1.hashCode(), stock2.hashCode())
    }

    @Test
    fun `stock toString should contain property values`() {
        val stock = Stock(code = "5678", name = "TestName", sector = sector)
        val str = stock.toString()
        assertTrue(str.contains("5678"))
        assertTrue(str.contains("TestName"))
    }

    // 異常系: code
    @Test
    fun `stock with blank code should have two violations`() {
    val stock = Stock(code = "", name = "Test", earningsDate = java.time.LocalDate.of(2025, 8, 27), sector = sector, minimalUnit = 200)
        val violations = validator.validate(stock)
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }
        assertTrue(messages.contains("銘柄コードは必須です"))
        assertTrue(messages.contains("銘柄コードは数字あるいはアルファベットのみで構成される必要があります"))
    }

    @Test
    fun `stock with invalid pattern code should have violation`() {
    val stock = Stock(code = "123-", name = "Test", earningsDate = java.time.LocalDate.of(2025, 8, 27), sector = sector, minimalUnit = 200)
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("銘柄コードは数字あるいはアルファベットのみで構成される必要があります", violations.first().message)
    }

    // 異常系: name
    @Test
    fun `stock with blank name should have violation`() {
    val stock = Stock(code = "1234", name = "", earningsDate = java.time.LocalDate.of(2025, 8, 27), sector = sector, minimalUnit = 200)
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("銘柄名は必須です", violations.first().message)
    }

    // currentPrice, incoming, minimalUnit の値テスト
    @Test
    fun `stock with negative currentPrice should be allowed`() {
        val stock = Stock(code = "1234", name = "Test", currentPrice = -100.0, sector = sector, minimalUnit = 200)
        val violations = validator.validate(stock)
        // currentPriceにバリデーションは無いので違反0
        assertEquals(0, violations.size)
    }

    @Test
    fun `stock with negative incoming should be allowed`() {
        val stock = Stock(code = "1234", name = "Test", incoming = -10.0, sector = sector)
        val violations = validator.validate(stock)
        // incomingにバリデーションは無いので違反0
        assertEquals(0, violations.size)
    }

    @Test
    fun `stock with custom minimalUnit should be set correctly`() {
        val stock = Stock(code = "1234", name = "Test", minimalUnit = 1, sector = sector)
        assertEquals(1, stock.minimalUnit)
    }
}
