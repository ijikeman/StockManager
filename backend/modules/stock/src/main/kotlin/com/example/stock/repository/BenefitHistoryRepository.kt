package com.example.stock.repository

import com.example.stock.model.BenefitHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BenefitHistoryRepository : JpaRepository<BenefitHistory, Int> {
    /* 株式ロットIDから優待利益履歴を検索 */
    fun findByStockLotId(stockLotId: Int): List<BenefitHistory>

    /* 売り取引IDから優待利益履歴を検索 */
    fun findBySellTransactionId(sellTransactionId: Int): List<BenefitHistory>
    
    /* 複数の株式ロットIDから優待利益履歴を一括検索（N+1問題回避） */
    fun findByStockLotIdIn(stockLotIds: List<Int>): List<BenefitHistory>
    
    /* 複数の売り取引IDから優待利益履歴を一括検索（N+1問題回避） */
    fun findBySellTransactionIdIn(sellTransactionIds: List<Int>): List<BenefitHistory>
}
