package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BuyTransactionRepository : JpaRepository<BuyTransaction, Int> {
    fun findByStockLotId(stockLotId: Int): List<BuyTransaction>

    fun findByStockLotIdOrderByTransactionDateAsc(stockLotId: Int): List<BuyTransaction>

    fun findFirstByStockLotOrderByTransactionDateAsc(stockLot: StockLot): BuyTransaction?
}
