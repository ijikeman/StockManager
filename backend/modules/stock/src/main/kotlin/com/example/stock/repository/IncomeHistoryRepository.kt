package com.example.stock.repository

import com.example.stock.model.IncomeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 収益履歴([IncomeHistory])のリポジトリインターフェース
 * Spring Data JPA を利用して、income_historyテーブルへのアクセスを提供します。
 */
@Repository
interface IncomeHistoryRepository : JpaRepository<IncomeHistory, Int> {
    /**
     * 指定された保有IDに紐づくすべての収益履歴を取得します。
     *
     * @param holdingId 検索対象の保有ID
     * @return 見つかった収益履歴のリスト
     */
    fun findByHoldingId(holdingId: Int): List<IncomeHistory>
}
