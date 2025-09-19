package com.example.stock.dto

import com.example.stock.model.LotStatus
import java.math.BigDecimal

data class HoldingInfoDTO(
    val id: Int,
    val owner_id: Int,
    val owner_name: String,
    val stock_code: String,
    val stock_name: String,
    val unit: Int,
    val quantity: Int,
    val is_nisa: Boolean,
    val status: LotStatus,
    val acquisition_price: BigDecimal,
    val current_price: BigDecimal,
    val profit_loss: BigDecimal,
    val dividend: BigDecimal,
    val minimalUnit: Int
)
