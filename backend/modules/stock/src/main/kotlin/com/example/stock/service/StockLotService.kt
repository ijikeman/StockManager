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

    fun getStockLot(id: Long): StockLotDetailResponse {
        val stockLot = stockLotRepository.findById(id.toInt())
            .orElseThrow { jakarta.persistence.EntityNotFoundException("StockLot not found with id: $id") }

        val buyTransaction = transactionRepository.findByStockLotAndType(stockLot, TransactionType.BUY)
            .firstOrNull() ?: throw IllegalStateException("Buy transaction not found for lot id: $id")

        return StockLotDetailResponse(
            id = stockLot.id.toLong(),
            unit = stockLot.unit,
            price = buyTransaction.price,
            is_nisa = stockLot.isNisa,
            stock = StockLotDetailResponse.StockInfo(
                code = stockLot.stock.code,
                name = stockLot.stock.name,
            ),
            owner = StockLotDetailResponse.OwnerInfo(
                id = stockLot.owner.id.toLong(),
                name = stockLot.owner.name,
            ),
        )
    }
}
