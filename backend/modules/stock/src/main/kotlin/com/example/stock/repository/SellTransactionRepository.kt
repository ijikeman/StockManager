package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.SellTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SellTransactionRepository : JpaRepository<SellTransaction, Int> {
    /* 買い取引IDから売り取引を検索 */
    fun findByBuyTransactionId(buyTransactionId: Int): List<SellTransaction>
    
    /* 複数の買い取引IDから売り取引を一括検索（N+1問題回避） */
    fun findByBuyTransactionIdIn(buyTransactionIds: List<Int>): List<SellTransaction>
}
