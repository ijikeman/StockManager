package com.example.stock.model

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OwnerTest {

    private lateinit var validator: Validator

    @BeforeEach
    fun setUp() {
        val factory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    // 正常系
    @Test
    fun `valid owner should have no violations`() {
        val owner = Owner(name = "John")
        val violations = validator.validate(owner)
        assertEquals(0, violations.size)
    }

    // 異常系
    // 名前が空欄
    @Test
    fun `With blank name should have two violations`() {
        val owner = Owner(name = "")
        val violations = validator.validate(owner)
        assertEquals(2, violations.size) // 違反の数が2である
        val messages = violations.map { it.message }
        assertTrue(messages.contains("名前は必須です"))
        assertTrue(messages.contains("名前はアルファベットのみで構成される必要があります"))
    }

    // 名前はアルファベットのみ
    @Test
    fun `owner with non-alphabetic name should have violation`() {
        val owner = Owner(name = "John123")
        val violations = validator.validate(owner)
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        assertEquals("名前はアルファベットのみで構成される必要があります", violation.message)
    }

    // 名前にスペースが含まれている
    @Test
    fun `owner with blank space name should have two violations`() {
        val owner = Owner(name = "Joh n")
        val violations = validator.validate(owner)
        assertEquals(1, violations.size)
        val messages = violations.map { it.message }
        assertTrue(messages.contains("名前はアルファベットのみで構成される必要があります"))
    }
}
