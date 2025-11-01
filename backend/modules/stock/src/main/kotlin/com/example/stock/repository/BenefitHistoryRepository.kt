package com.example.stock.repository

import com.example.stock.model.BenefitHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BenefitHistoryRepository : JpaRepository<BenefitHistory, Int> {
    /* 株式ロットIDから優待利益履歴を検索 */
    fun findByStockLotId(stockLotId: Int): List<BenefitHistory>

    /* 株式ロットIDのリストから優待利益履歴を一括検索 */
    fun findByStockLotIdIn(stockLotIds: List<Int>): List<BenefitHistory>

    /* 売り取引IDから優待利益履歴を検索 */
    fun findBySellTransactionId(sellTransactionId: Int): List<BenefitHistory>
}
