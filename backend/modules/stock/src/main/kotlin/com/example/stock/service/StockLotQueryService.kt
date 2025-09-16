package com.example.stock.service

import com.example.stock.dto.StockLotDTO
import com.example.stock.model.LotStatus
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.example.stock.model.StockLot

/**
 * 株式ロットのクエリサービス。
 * 株式ロットの情報を取得するための読み取り専用操作を提供します。
 */
@Service
@Transactional(readOnly = true)
class StockLotQueryService(
    private val stockLotRepository: StockLotRepository
) {
    private fun StockLot.toDto(): StockLotDTO {
        return StockLotDTO(
            id = this.id,
            owner_id = this.owner.id,
            owner_name = this.owner.name,
            stock_code = this.stock.code,
            stock_name = this.stock.name,
            unit = this.unit,
            quantity = this.unit * this.stock.minumalUnit,
            is_nisa = this.isNisa,
            status = this.status
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
        return stockLotRepository.findByStatus(LotStatus.HOLDING)
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
        return stockLotRepository.findByOwnerIdAndStatus(ownerId, LotStatus.HOLDING)
            .map { it.toDto() }
            .groupBy { it.is_nisa }
    }
}
