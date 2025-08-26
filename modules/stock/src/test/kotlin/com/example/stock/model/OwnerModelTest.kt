package com.example.stock.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory

class OwnerModelTest {
    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `valid owner should pass validation`() {
        val owner = Owner(name = "John")
        val violations = validator.validate(owner)
        assertTrue(violations.isEmpty())
    }

    @Test
    fun `blank name should fail validation`() {
        val owner = Owner(name = "")
        val violations = validator.validate(owner)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }

    @Test
    fun `name with non-alphabet should fail validation`() {
        val owner = Owner(name = "John123")
        val violations = validator.validate(owner)
        assertTrue(violations.any { it.propertyPath.toString() == "name" })
    }
}
