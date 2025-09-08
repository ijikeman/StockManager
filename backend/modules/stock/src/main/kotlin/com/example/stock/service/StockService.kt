package com.example.stock.service

import com.example.stock.model.Stock
import com.example.stock.repository.StockRepository
import com.example.stock.provider.YahooFinanceProvider
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

/**
 * 株式を管理するためのサービスクラスです。
 *
 * @property stockRepository 株式データにアクセスするためのリポジトリ。
 * @property yahooFinanceProvider YahooFinanceから株価を取得するためのプロバイダー。
 */
@Service
class StockService(
    private val stockRepository: StockRepository,
    private val yahooFinanceProvider: YahooFinanceProvider
    ) {

    /**
     * すべての株式のリストを返します。
     *
     * @return すべての株式のリスト。
     */
    fun findAll(): List<Stock> {
        return stockRepository.findAll()
    }

    /**
     * コードによって株式を検索します。
     *
     * @param code 検索する株式のコード。
     * @return 指定されたコードの株式。見つからない場合はnull。
     */
    fun findByCode(code: String): Stock? {
        return stockRepository.findByCode(code)
    }

    /**
     * IDによって株式を検索します。
     *
     * @param id 検索する株式のID。
     * @return 指定されたIDの株式。見つからない場合はnull。
     */
    fun findById(id: Int): Stock? {
        return stockRepository.findById(id).orElse(null)
    }

    
    // 追加する
    open fun save(stock: Stock): Stock {
        return stockRepository.save(stock)
    }

    // 削除する
    open fun deleteById(id: Int) {
        stockRepository.deleteById(id)
    }

    /**
     * 銘柄の株価を更新します。
     *
     * @param code 更新する株式のコード。
     * @return 更新された株式。見つからない場合はnull。
     */
    fun updateStockPrice(code: String): Stock? {
        val stockInfo = yahooFinanceProvider.fetchStockInfo(code)
        if (stockInfo != null) {
            val stock = stockRepository.findByCode(code)
            if (stock != null) {
                val updatedStock = stock.copy(
                    current_price = stockInfo.price ?: stock.current_price,
                    dividend = stockInfo.dividend ?: stock.dividend,
                    earnings_date = stockInfo.earnings_date ?: stock.earnings_date
                )
                return stockRepository.save(updatedStock)
            }
        }
        return null
    }

    /**
     * すべての銘柄の株価を更新します。
     *
     * @return 更新された株式のリスト。
     */
    fun updateAllStockPrices(): List<Stock> {
        val stocks = stockRepository.findAll()
        return stocks.mapNotNull { stock ->
            updateStockPrice(stock.code)
        }
    }

    /**
     * 銘柄名を取得します。
     *
     * @param code 取得する株式のコード。
     * @return 株式の名前。見つからない場合はnull。
     */
    fun fetchStockName(code: String): String? {
        return yahooFinanceProvider.fetchStockName(code)
    }
}
