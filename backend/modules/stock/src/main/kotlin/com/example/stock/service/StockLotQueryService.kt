package com.example.stock.service

import com.example.stock.dto.StockLotDTO
import com.example.stock.dto.StockLotDetailResponse
import com.example.stock.model.StockLot
import com.example.stock.model.TransactionType
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 株式ロットのクエリサービス。
 * 株式ロットの情報を取得するための読み取り専用操作を提供します。
 */
@Service
@Transactional(readOnly = true)
class StockLotQueryService(
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository,
) {
    private fun StockLot.toDto(): StockLotDTO {
        return StockLotDTO(
            id = this.id,
            owner_id = this.owner.id,
            owner_name = this.owner.name,
            stock_code = this.stock.code,
            stock_name = this.stock.name,
            unit = this.unit,
            quantity = this.unit * this.stock.minimalUnit,
            is_nisa = this.isNisa,
            minimalUnit = this.stock.minimalUnit,
        )
    }

    /**
     * すべての株式ロットを取得します。
     *
     * @return StockLotDTOのリスト
     */
    fun findAllStockLots(): List<StockLotDTO> {
        return stockLotRepository.findAll().map { it.toDto() }
    }

    /**
     * 保有中の株式ロットをNISA区分でグループ化して取得します。
     *
     * @return NISA区分をキー、StockLotDTOのリストを値とするマップ
     */
    fun findHoldingStockLots(): Map<Boolean, List<StockLotDTO>> {
        return stockLotRepository.findAll()
            .map { it.toDto() }
            .groupBy { it.is_nisa }
    }

    /**
     * 指定された所有者の保有中の株式ロットをNISA区分でグループ化して取得します。
     *
     * @param ownerId 所有者のID
     * @return NISA区分をキー、StockLotDTOのリストを値とするマップ
     */
    fun findHoldingStockLotsByOwner(ownerId: Int): Map<Boolean, List<StockLotDTO>> {
        return stockLotRepository.findByOwnerId(ownerId)
            .map { it.toDto() }
            .groupBy { it.is_nisa }
    }

    /**
     * 指定されたIDの株式ロットを取得します。
     *
     * @param lotId ロットのID
     * @return StockLotDTO
     */
    fun findStockLotById(lotId: Int): StockLotDTO {
        return stockLotRepository.findById(lotId)
            .map { it.toDto() }
            .orElseThrow { jakarta.persistence.EntityNotFoundException("StockLot not found with id: $lotId") }
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