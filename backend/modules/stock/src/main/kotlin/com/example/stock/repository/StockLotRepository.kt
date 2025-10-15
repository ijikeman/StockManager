package com.example.stock.repository

import com.example.stock.model.StockLot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockLotRepository : JpaRepository<StockLot, Int> {
    /* 所有者IDから株式ロットを検索 */
    fun findByOwnerId(ownerId: Int): List<StockLot>

    /* 所有者IDと現在の単位数が指定値より大きい株式ロットを検索 */
    fun findByOwnerIdAndCurrentUnitGreaterThan(ownerId: Int, currentUnit: Int): List<StockLot>

    /* 現在の単位数が指定値より大きい株式ロットをすべて検索 */
    fun findAllByCurrentUnitGreaterThan(currentUnit: Int): List<StockLot>
}
