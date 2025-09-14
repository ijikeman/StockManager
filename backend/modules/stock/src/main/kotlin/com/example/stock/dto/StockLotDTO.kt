package com.example.stock.dto

import com.example.stock.model.LotStatus

data class StockLotDTO(
    val id: Int,
    val owner_id: Int,
    val owner_name: String,
    val stock_code: String,
    val stock_name: String,
    val quantity: Int,
    val is_nisa: Boolean,
    val status: LotStatus
)
