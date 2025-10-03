package com.example.stock.repository

import com.example.stock.model.IncomingHistory
import com.example.stock.model.StockLot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomingHistoryRepository : JpaRepository<IncomingHistory, Int> {
    fun findByStockLotId(stockLotId: Int): List<IncomingHistory>
    fun findBySellTransactionId(sellTransactionId: Int): List<IncomingHistory>
}
