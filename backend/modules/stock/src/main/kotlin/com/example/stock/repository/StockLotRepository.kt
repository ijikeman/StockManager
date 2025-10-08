package com.example.stock.repository

import com.example.stock.model.StockLot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockLotRepository : JpaRepository<StockLot, Int> {
    fun findByOwnerId(ownerId: Int): List<StockLot>

    fun findByOwnerIdAndCurrentUnitGreaterThan(ownerId: Int, currentUnit: Int): List<StockLot>

    fun findAllByCurrentUnitGreaterThan(currentUnit: Int): List<StockLot>
}
