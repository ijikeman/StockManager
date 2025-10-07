package com.example.stock.dto

data class StockLotResponseDto(
    val id: Int,
    val owner: OwnerDto,
    val stock: StockDto,
    val currentUnit: Int
)