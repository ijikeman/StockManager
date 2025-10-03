package com.example.stock.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

/**
 * DTO for representing a transaction in API responses.
 */
data class BuyTransactionDTO(
    val id: Int,

    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    val type: String,
    val stock: StockInfoDTO,
    val owner_id: Int,
    val unit: Int,
    val price: Double,
    val fees: Double,
)

/**
 * DTO for nested stock information within TransactionDTO.
 */
data class StockInfoDTO(
    val code: String,
    val name: String
)
