package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

data class StockLotResponseDto(
    val id: Int,
    val owner: OwnerDto,
    val stock: StockDto,
    val currentUnit: Int,
    val averagePrice: BigDecimal,
    val purchaseDate: LocalDate?,
    val incoming: BigDecimal
)