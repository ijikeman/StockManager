package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.Transaction
import com.example.stock.model.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BuyTransactionRepository : JpaRepository<Transaction, Int> {
    fun findByStockId(stockId: Int): List<Transaction>
}
