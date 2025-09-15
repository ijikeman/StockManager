package com.example.stock.service

import com.example.stock.dto.StockLotDTO
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
     * StockLotエンティティをStockLotDTOに変換します。
     *
     * @return StockLotDTO
     */
    private fun StockLot.toDTO() = StockLotDTO(
        id = this.id,
        owner_id = this.owner.id,
        owner_name = this.owner.name,
        stock_code = this.stock.code,
        stock_name = this.stock.name,
        quantity = this.quantity,
        is_nisa = this.isNisa,
        status = this.status
    )

    /**
     * すべての株式ロットを取得します。
     *
     * @return StockLotDTOのリスト
     */
    fun findAllStockLots(): List<StockLotDTO> {
        return stockLotRepository.findAll().map { it.toDTO() }
    }
}
