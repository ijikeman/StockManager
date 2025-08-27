package com.example.stock.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StockTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    // 正常系
    @Test
    fun `valid stock should have no violations`() {
        val stock = Stock(
            code = "7203",
            name = "トヨタ自動車",
            current_price = 9000.0,
            dividend = 150.0,
            release_date = "20250827"
        )
        val violations = validator.validate(stock)
        assertEquals(0, violations.size)
    }

    // 異常系: code
    @Test
    fun `stock with blank code should have two violations`() {
        val stock = Stock(code = "", name = "Test", release_date = "20250827")
        val violations = validator.validate(stock)
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }
        assertTrue(messages.contains("銘柄コードは必須です"))
        assertTrue(messages.contains("名前は数字あるいはアルファベットのみで構成される必要があります"))
    }

    @Test
    fun `stock with invalid pattern code should have violation`() {
        val stock = Stock(code = "123-", name = "Test", release_date = "20250827")
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("名前は数字あるいはアルファベットのみで構成される必要があります", violations.first().message)
    }

    // 異常系: name
    @Test
    fun `stock with blank name should have violation`() {
        val stock = Stock(code = "1234", name = "", release_date = "20250827")
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("銘柄名は必須です", violations.first().message)
    }

    // 異常系: release_date
    @Test
    fun `stock with invalid release_date format should have violation`() {
        val stock = Stock(code = "1234", name = "Test", release_date = "2025/01/01")
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("日付はYYYYMMDDの形式で入力してください", violations.first().message)
    }

    @Test
    fun `stock with short release_date should have violation`() {
        val stock = Stock(code = "1234", name = "Test", release_date = "2025")
        val violations = validator.validate(stock)
        assertEquals(1, violations.size)
        assertEquals("日付はYYYYMMDDの形式で入力してください", violations.first().message)
    }
}
