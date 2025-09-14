package com.example.stock.repository

import com.example.stock.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : JpaRepository<Transaction, Int> {
    fun findByStockLotId(stockLotId: Int): List<Transaction>
}
