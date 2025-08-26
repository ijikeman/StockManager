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

    @Test
    fun `valid owner should have no violations`() {
        val owner = Owner(name = "Keiji")
        val violations = validator.validate(owner)
        assertEquals(0, violations.size)
    }

    @Test
    fun `owner with blank name should have two violations`() {
        val owner = Owner(name = "")
        val violations = validator.validate(owner)
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }
        assertTrue(messages.contains("名前は必須です"))
        assertTrue(messages.contains("名前はアルファベットのみで構成される必要があります"))
    }

    @Test
    fun `owner with non-alphabetic name should have violation`() {
        val owner = Owner(name = "Keiji123")
        val violations = validator.validate(owner)
        assertEquals(1, violations.size)
        val violation = violations.iterator().next()
        assertEquals("名前はアルファベットのみで構成される必要があります", violation.message)
    }

    @Test
    fun `owner with blank space name should have two violations`() {
        val owner = Owner(name = " ")
        val violations = validator.validate(owner)
        assertEquals(2, violations.size)
        val messages = violations.map { it.message }
        assertTrue(messages.contains("名前は必須です"))
        assertTrue(messages.contains("名前はアルファベットのみで構成される必要があります"))
    }
}
