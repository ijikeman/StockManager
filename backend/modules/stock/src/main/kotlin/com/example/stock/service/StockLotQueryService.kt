package com.example.stock.service

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
    /**
     * すべての株式ロットを取得します。
     *
     * @return StockLotDTOのリスト
     */
    fun findAllStockLots(): List<StockLot> {
        return stockLotRepository.findAll()
    }

    /**
     * 保有中の株式ロットをNISA区分でグループ化して取得します。
     *
     * @return NISA区分をキー、StockLotDTOのリストを値とするマップ
     */
    fun findHoldingStockLots(): Map<Boolean, List<StockLot>> {
        return stockLotRepository.findByStatus(LotStatus.HOLDING)
            .groupBy { it.isNisa }
    }

    /**
     * 指定された所有者の保有中の株式ロットをNISA区分でグループ化して取得します。
     *
     * @param ownerId 所有者のID
     * @return NISA区分をキー、StockLotDTOのリストを値とするマップ
     */
    fun findHoldingStockLotsByOwner(ownerId: Int): Map<Boolean, List<StockLot>> {
        return stockLotRepository.findByOwnerIdAndStatus(ownerId, LotStatus.HOLDING)
            .groupBy { it.isNisa }
    }
}
