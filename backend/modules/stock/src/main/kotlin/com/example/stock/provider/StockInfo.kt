package com.example.stock.provider

import java.time.LocalDate

/* 
銘柄の以下のデータを保持
- price: 現在の価格
- dividend: 配当金
- earningsDate: 業績発表日
*/
data class StockInfo(
    val price: Double?,
    val dividend: Double?,
    val earningsDate: LocalDate?
)
