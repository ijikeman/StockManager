package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class IncomingHistoryAddDto(
    val paymentDate: LocalDate,
    val lotId: Int,
    val incoming: BigDecimal,
    val isNisa: Boolean = false
)