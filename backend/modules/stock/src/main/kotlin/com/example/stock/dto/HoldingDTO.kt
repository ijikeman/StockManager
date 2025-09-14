package com.example.stock.dto

import java.math.BigDecimal

data class HoldingDTO(
    val stockCode: String,
    val stockName: String,
    val quantity: Int,
    val averagePrice: BigDecimal,
    val bookValue: BigDecimal,
)
