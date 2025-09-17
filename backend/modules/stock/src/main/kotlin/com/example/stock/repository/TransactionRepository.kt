package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.Transaction
import com.example.stock.model.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Int> {
    fun findByStockLotId(stockLotId: Int): List<Transaction>

    fun findByStockLotAndType(stockLot: StockLot, type: TransactionType): List<Transaction>
}
