package com.example.stock.dto

import java.math.BigDecimal

data class StockLotResponseDto(
    val id: Int,
    val owner: OwnerDto,
    val stock: StockDto,
    val currentUnit: Int,
    val averagePrice: BigDecimal
)