package com.example.stock.dto

import java.math.BigDecimal

data class StockLotResponse(
    val id: Long,
    val stockName: String,
    val stockCode: String,
    val price: BigDecimal,
    val unit: Int,
    val isNisa: Boolean,
)

data class StockLotDetailResponse(
    val id: Long,
    val unit: Int,
    val price: BigDecimal,
    val is_nisa: Boolean,
    val stock: StockInfo,
    val owner: OwnerInfo,
) {
    data class StockInfo(
        val code: String,
        val name: String,
    )

    data class OwnerInfo(
        val id: Long,
        val name: String,
    )
}