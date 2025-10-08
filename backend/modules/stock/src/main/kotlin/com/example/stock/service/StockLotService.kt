package com.example.stock.service

import com.example.stock.dto.OwnerDto
import com.example.stock.dto.StockDto
import com.example.stock.dto.StockLotResponseDto
import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 株式ロットのサービス。
 * 株式ロットに関するビジネスロジックを処理します。
 */
@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository,
    private val buyTransactionService: BuyTransactionService,
    private val buyTransactionRepository: BuyTransactionRepository,
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

    fun findAllWithAveragePrice(): List<StockLotResponseDto> {
        val stockLots = stockLotRepository.findAll()
        return stockLots.map { stockLot ->
            createDtoWithAveragePrice(stockLot)
        }
    }

    fun findByIdWithAveragePrice(id: Int): StockLotResponseDto? {
        val stockLot = stockLotRepository.findById(id).orElse(null)
        return stockLot?.let { createDtoWithAveragePrice(it) }
    }

    private fun createDtoWithAveragePrice(stockLot: StockLot): StockLotResponseDto {
        val transactions = buyTransactionRepository.findByStockLotId(stockLot.id)
        val averagePrice = if (transactions.isNotEmpty()) {
            val totalCost = transactions.fold(BigDecimal.ZERO) { acc, tx ->
                acc.add(tx.price.multiply(BigDecimal(tx.unit))).add(tx.fee)
            }
            val totalUnits = transactions.sumOf { it.unit }
            if (totalUnits > 0) {
                totalCost.divide(BigDecimal(totalUnits), 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }
        } else {
            BigDecimal.ZERO
        }

        return StockLotResponseDto(
            id = stockLot.id,
            owner = OwnerDto(id = stockLot.owner.id, name = stockLot.owner.name),
            stock = StockDto(
                id = stockLot.stock.id,
                code = stockLot.stock.code,
                name = stockLot.stock.name,
                currentPrice = stockLot.stock.currentPrice,
                minimalUnit = stockLot.stock.minimalUnit,
            ),
            currentUnit = stockLot.currentUnit,
            averagePrice = averagePrice,
        )
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
    fun createStockLot(
        owner: Owner,
        stock: Stock,
        currentUnit: Int,
    ): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit,
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
        buyTransaction: BuyTransaction,
    ): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            currentUnit = currentUnit,
        )
        val savedStockLot = stockLotRepository.save(stockLot)
        // BuyTransactionのstockLotを保存したものに差し替えて作成
        val transactionToSave = buyTransaction.copy(stockLot = savedStockLot)
        buyTransactionService.create(transactionToSave)
        return savedStockLot
    }
}