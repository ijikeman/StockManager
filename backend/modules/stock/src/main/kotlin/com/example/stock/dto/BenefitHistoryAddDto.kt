package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class BenefitHistoryAddDto(
    val paymentDate: LocalDate,
    val lotId: Int,
    val benefit: BigDecimal
)