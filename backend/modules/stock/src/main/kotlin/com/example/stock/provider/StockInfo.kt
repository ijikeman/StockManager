package com.example.stock.provider

import java.time.LocalDate

/* 
銘柄の以下のデータを保持
- price: 現在の価格
- incoming: 配当金
- earnings_date: 業績発表日
*/
data class StockInfo(
    val price: Double?,
    val incoming: Double?,
    val earningsDate: LocalDate?
)
