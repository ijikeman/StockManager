package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class IncomingHistoryDTO(
    val id: Int,
    val lot_id: Int,
    val incoming: BigDecimal,
    val payment_date: LocalDate
)
