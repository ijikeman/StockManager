package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 株式ロットのサービス。
 * 株式ロットに関するビジネスロジックを処理します。
 */
@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository
) {
    /**
     * 所有者IDによって株式ロットを検索します。
     *
     * @param ownerId 所有者ID
     * @return StockLotのリスト
     */
    fun findByOwnerId(ownerId: Int): List<StockLot> {
        return stockLotRepository.findByOwnerId(ownerId)
    }

    /**
     * 単一の株式ロットを作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param isNisa NISA口座かどうか
     * @param unit 単元数
     * @return 作成されたStockLot
     */
    fun createStockLot(owner: Owner, stock: Stock, isNisa: Boolean, unit: Int): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            isNisa = isNisa,
            unit = unit
        )
        return stockLotRepository.save(stockLot)
    }
}
