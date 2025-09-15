package com.example.stock.repository

import com.example.stock.model.StockLot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockLotRepository : JpaRepository<StockLot, Int> {
    fun findByOwnerId(ownerId: Int): List<StockLot>

    // 追加：ownerIdとstatusで絞り込むメソッド
    fun findByOwnerIdAndStatus(ownerId: Int, status: String): List<StockLot>
}
