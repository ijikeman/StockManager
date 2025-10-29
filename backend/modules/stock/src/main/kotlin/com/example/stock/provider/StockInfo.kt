package com.example.stock.provider

import java.time.LocalDate

/* 
銘柄の以下のデータを保持
- price: 現在の価格
- incoming: 配当金
- earnings_date: 業績発表日
- previousPrice: 前日終値
- latestDisclosureDate: 最新の適時開示日
*/
data class StockInfo(
    val price: Double?,
    val incoming: Double?,
    val earningsDate: LocalDate?,
    val previousPrice: Double?,
    val latestDisclosureDate: LocalDate?
)
