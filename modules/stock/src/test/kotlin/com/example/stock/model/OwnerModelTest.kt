package com.example.stock.model

import org.junit.jupiter.api.Assertions.* // springboot.frameworkに含まれている？
import org.junit.jupiter.api.Test
import jakarta.validation.Validation
import jakarta.validation.Validator

class OwnerModelTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    // --- 正常系
    @Test
    fun `Owner正常系`() {
        val owner = Owner(1, "JohnDoe")
        val violations = validator.validate(owner)
        assertTrue(violations.isEmpty())
        assertEquals(owner.name, "JohnDoe")
    }

    // --- 異常系
    @Test
    fun `Owner異常系 -name NotBlank-`() {
        val owner = Owner(2, "")
        val violations = validator.validate(owner)
        assertFalse(violations.isEmpty())
        assertEquals("名前は必須です", violations.first().message)
    }

    @Test
    fun `Owner異常系 -name input validation-`() {
        val owner = Owner(2, "John Doe")
        val violations = validator.validate(owner)
        assertFalse(violations.isEmpty())
        // "名前はアルファベットのみで構成される必要があります"のエラーメッセージが返ってくること
        assertEquals("名前はアルファベットのみで構成される必要があります", violations.first().message)
    }
}
