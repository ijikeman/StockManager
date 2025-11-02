package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 損益ページ用のDTO
 * StockLotから銘柄名と売却損益情報を表示する
 */
data class ProfitlossStockLotDto(
    val stockCode: String, // 銘柄コード
    val stockName: String, // 銘柄名
    val purchasePrice: Double, // 購入価格
    val currentPrice: Double? = null, // 現在価格
    val currentUnit: Int? = null, // 現在の単元数
    val totalIncoming: BigDecimal? = null, // 総配当金額
    val totalBenefit: BigDecimal? = null, // 総優待金額
    val buyTransactionDate: LocalDate? = null, // 購入取引日
)
