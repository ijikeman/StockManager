package com.example.stock.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SectorTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    // 正常系
    @Test
    fun `valid sector should have no violations`() {
        val sector = Sector(name = "不動産")
        val violations = validator.validate(sector)
        assertEquals(0, violations.size)
    }

    // 異常系
    // 名前が空欄
    @Test
    fun `With blank name should have two violations`() {
        val sector = Sector(name = "")
        val violations = validator.validate(sector)
        assertEquals(1, violations.size) // 違反の数が1である
        val messages = violations.map { it.message }
        assertTrue(messages.contains("セクター名は必須です"))
    }
}
