package com.example.stock.repository

import com.example.stock.model.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 取引履歴([Transaction])のリポジトリインターフェース
 * Spring Data JPA を利用して、transactionテーブルへのアクセスを提供します。
 */
@Repository
interface TransactionRepository : JpaRepository<Transaction, Int> {
    /**
     * 指定された保有IDに紐づくすべての取引履歴を取得します。
     *
     * @param holdingId 検索対象の保有ID
     * @return 見つかった取引履歴のリスト
     */
    fun findByHoldingId(holdingId: Int): List<Transaction>
}
