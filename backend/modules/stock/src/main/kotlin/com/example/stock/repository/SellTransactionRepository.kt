package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.SellTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SellTransactionRepository : JpaRepository<SellTransaction, Int> {
    fun findByStockId(stockId: Int): List<SellTransaction>
}
