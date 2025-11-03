package com.example.stock.dto

import java.math.BigDecimal
import java.time.LocalDate

/**
 * 損益ページ用のDTO
 * StockLotから銘柄名と売却損益情報を表示する
 */
data class ProfitlossDto(
    val stockCode: String, // 銘柄コード
    val stockName: String, // 銘柄名
    val minimalUnit: Int, // 最小単元
    val purchasePrice: Double, // 購入価格
    val sellPrice: Double? = null, // 売却価格
    val sellUnit: Int? = null, // 売却単元数
    val profitLoss: BigDecimal? = null, // 損益 ((売却価格 - 購入価格) * 単元数 * 最小単元 - 購入手数料 - 売却手数料)
    val buyTransactionDate: LocalDate? = null, // 購入取引日
    val sellTransactionDate: LocalDate? = null, // 売却取引日
    val ownerName: String? = null // 所有者名
)
