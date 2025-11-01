package com.example.stock.repository

import com.example.stock.model.IncomingHistory
import com.example.stock.model.StockLot
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomingHistoryRepository : JpaRepository<IncomingHistory, Int> {
    /* 株式ロットIDから配当履歴を検索 */
    fun findByStockLotId(stockLotId: Int): List<IncomingHistory>

    /* 株式ロットIDのリストから配当履歴を一括検索 */
    fun findByStockLotIdIn(stockLotIds: List<Int>): List<IncomingHistory>

    /* 売り取引IDから配当履歴を検索 */
    fun findBySellTransactionId(sellTransactionId: Int): List<IncomingHistory>
}
