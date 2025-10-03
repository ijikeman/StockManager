package com.example.stock.dto

data class CreateStockLotRequest(
    val ownerId: Int,
    val stockId: Int,
    val unit: Int,
    val isNisa: Boolean
)