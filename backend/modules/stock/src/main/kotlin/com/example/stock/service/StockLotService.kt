package com.example.stock.service

import com.example.stock.dto.CreateStockLotRequest
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.OwnerRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.StockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 株式ロットのサービス。
 * 株式ロットに関するビジネスロジックを処理します。
 */
@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository,
    private val ownerRepository: OwnerRepository,
    private val stockRepository: StockRepository
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

    /**
     * 株式ロットを更新します。
     *
     * @param id 株式ロットID
     * @param unit 単元数
     * @param isNisa NISAかどうか
     * @return 更新されたStockLot
     */
    fun updateStockLot(id: Int, unit: Int, isNisa: Boolean): StockLot {
        val stockLot = stockLotRepository.findById(id)
            .orElseThrow { jakarta.persistence.EntityNotFoundException("StockLot not found with id: $id") }
        stockLot.update(unit, isNisa)
        return stockLotRepository.save(stockLot)
    }

    /**
     * 株式ロットを削除します。
     *
     * @param id 株式ロットID
     */
    fun deleteStockLot(id: Int) {
        stockLotRepository.deleteById(id)
    }

    /**
     * 新しい株式ロットを作成します。
     *
     * @param request 株式ロット作成リクエスト
     * @return 作成されたStockLot
     */
    fun createStockLot(request: CreateStockLotRequest): StockLot {
        val owner = ownerRepository.findById(request.ownerId)
            .orElseThrow { jakarta.persistence.EntityNotFoundException("Owner not found with id: ${request.ownerId}") }
        val stock = stockRepository.findById(request.stockId)
            .orElseThrow { jakarta.persistence.EntityNotFoundException("Stock not found with id: ${request.stockId}") }

        return createStockLot(owner, stock, request.isNisa, request.unit)
    }
}
