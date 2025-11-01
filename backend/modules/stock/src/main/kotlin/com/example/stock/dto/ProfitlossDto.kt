package com.example.stock.dto

/**
 * 損益ページ用のDTO
 * StockLotから銘柄名のみを表示する
 */
data class ProfitlossDto(
    val stockCode: String, // 銘柄コード
    val stockName: String, // 銘柄名
    val purchasePrice: Double, // 購入価格
    val totalDividend: Double, // 総配当金額
    val totalBenefit: Double // 総優待金額
)
