package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.BuyTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BuyTransactionRepository : JpaRepository<BuyTransaction, Int> {
    /* 株式ロットIDから買い取引を検索 */
    fun findByStockLotId(stockLotId: Int): List<BuyTransaction>

    /* 株式ロットIDから買い取引を取引日順に昇順で検索 */
    fun findByStockLotIdOrderByTransactionDateAsc(stockLotId: Int): List<BuyTransaction>

    /* 株式ロットから最も古い買い取引を検索 */
    fun findFirstByStockLotOrderByTransactionDateAsc(stockLot: StockLot): BuyTransaction?
}
