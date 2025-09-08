package com.example.stock.provider

import com.example.stock.provider.StockInfo

interface FinanceProvider {
    /**
     * 指定した銘柄コードと国に対応する現在の株価を返します。
     *
     * @param code 銘柄コード
     * @return 現在の株価（取得できない場合は null）
     */
    fun fetchStockInfo(code: String): StockInfo?
    /**
     * 指定した銘柄コードに対応する銘柄名を返します。
     *
     * @param code 銘柄コード
     * @return 現在の株価（取得できない場合は null）
     */
    fun fetchStockName(code: String): String?
}
