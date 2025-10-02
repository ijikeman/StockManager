package com.example.stock.dto

data class UpdateStockLotRequest(
    val unit: Int,
    val isNisa: Boolean,
)