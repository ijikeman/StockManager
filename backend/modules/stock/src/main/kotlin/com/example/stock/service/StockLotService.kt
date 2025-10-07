package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
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
    private val stockLotRepository: StockLotRepository,
    private val buyTransactionService: BuyTransactionService
) {
    /**
     * すべての株式ロットを取得します。
     * @return StockLotのリスト
     */
    open fun findAll(): List<StockLot> {
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
     * IDに基づいてStockLotを検索します。
     * @param id 検索するStockLotのID
     * @return 見つかったStockLot。存在しない場合はnull。
     */
    open fun findById(id: Int): StockLot? {
        return stockLotRepository.findById(id).orElse(null)
    }

    /**
     * すべての株式ロットからcurrentUnitが0以外の株式ロットを抽出します。
     * @return currentUnitが0以外のStockLotリスト
     */
    fun findAllWithUnit(): List<StockLot> {
        return stockLotRepository.findAll().filter { it.currentUnit != 0 }
    }

    /**
     * 指定した所有者IDのうち、currentUnitが0以外の株式ロットを抽出します。
     * @param ownerId 所有者ID
     * @return currentUnitが0以外のStockLotリスト
     */
    fun findByOwnerIdWithUnit(ownerId: Int): List<StockLot> {
        return stockLotRepository.findByOwnerId(ownerId).filter { it.currentUnit != 0 }
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

    /**
     * 単一の株式ロットを作成し、同時に購入取引も作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param currentUnit 現在の単元数
     * @param buyTransaction 作成する購入取引（stockLotは無視される）
     * @return 作成されたStockLot
     */
    fun createStockLotAndBuyTransaction(
        owner: Owner,
        stock: Stock,
        currentUnit: Int,
        buyTransaction: BuyTransaction
    ): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit
        )
        val savedStockLot = stockLotRepository.save(stockLot)
        // BuyTransactionのstockLotを保存したものに差し替えて作成
        val transactionToSave = buyTransaction.copy(stockLot = savedStockLot)
        buyTransactionService.create(transactionToSave)
        return savedStockLot
    }
}
