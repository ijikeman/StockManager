package com.example.stock.dto

data class StockDto(
    val id: Int,
    val code: String,
    val name: String,
    val currentPrice: Double,
    val minimalUnit: Int,
    val previousClose: Double
)
