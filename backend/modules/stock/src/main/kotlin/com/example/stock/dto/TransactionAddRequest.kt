package com.example.stock.dto

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate

/**
 * DTO for creating or updating a transaction from an API request.
 */
data class TransactionAddRequest(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @field:NotBlank(message = "Type is mandatory")
    val type: String,

    @field:NotBlank(message = "Stock code is mandatory")
    val stock_code: String,

    @field:NotNull(message = "Owner ID is mandatory")
    val owner_id: Int,

    @field:NotNull(message = "Unit is mandatory")
    @field:Positive(message = "Unit must be positive")
    val unit: Int,

    @field:NotNull(message = "Price is mandatory")
    @field:Positive(message = "Price must be positive")
    val price: Double,

    @field:NotNull(message = "Fees are mandatory")
    val fees: Double,

    val nisa: Boolean = false,

    val lot_id: Int? = null
)
