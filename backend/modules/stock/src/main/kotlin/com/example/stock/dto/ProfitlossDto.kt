package com.example.stock.dto

/**
 * 損益ページ用のDTO
 * StockLotから銘柄名のみを表示する
 */
data class ProfitlossDto(
    val stockCode: String,
    val stockName: String
)
