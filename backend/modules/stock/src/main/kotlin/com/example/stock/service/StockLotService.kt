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
     * すべての株式ロットを取得します。
     * @return StockLotのリスト
     */
    fun findAllStockLots(): List<StockLot> {
        return stockLotRepository.findAll()
    }

    /**
     * 所有者IDによって株式ロットを検索します。
     * @param ownerId 所有者ID
     * @return StockLotのリスト
     */
    fun findByOwnerId(ownerId: Int): List<StockLot> {
        return stockLotRepository.findByOwnerId(ownerId)
    }

    /**
     * 指定されたIDの株式ロットを取得します。
     * @param lotId ロットのID
     * @return StockLot
     */
    fun findStockLotById(lotId: Int): StockLot {
        return stockLotRepository.findById(lotId)
            .orElseThrow { jakarta.persistence.EntityNotFoundException("StockLot not found with id: $lotId") }
    }

    /**
     * 単一の株式ロットを作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param currentUnit 現在の単元数
     * @return 作成されたStockLot
     */
    fun createStockLot(owner: Owner, stock: Stock, currentUnit: Int): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit
        )
        return stockLotRepository.save(stockLot)
    }
}
