package com.example.stock.service

import com.example.stock.model.Stock
import com.example.stock.repository.StockRepository
import org.springframework.stereotype.Service

/**
 * 株式を管理するためのサービスクラスです。
 *
 * @property stockRepository 株式データにアクセスするためのリポジトリ。
 */
@Service
class StockService(private val stockRepository: StockRepository) {
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
}
