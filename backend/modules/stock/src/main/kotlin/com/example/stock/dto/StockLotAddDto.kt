package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class StockLotAddDto(
    val ownerId: Int,
    val stockId: Int,
    val unit: Int,
    val price: BigDecimal,
    val fee: BigDecimal,
    val isNisa: Boolean,
    val transactionDate: LocalDate
)