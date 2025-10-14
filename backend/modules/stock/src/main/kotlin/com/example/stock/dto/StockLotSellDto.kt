package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class StockLotSellDto(
    val unit: Int,
    val price: BigDecimal,
    val fee: BigDecimal,
    val transactionDate: LocalDate
)