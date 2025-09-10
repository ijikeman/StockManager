package com.example.stock.service

import com.example.stock.model.Transaction
import com.example.stock.repository.TransactionRepository
import org.springframework.stereotype.Service

/**
 * 取引履歴([Transaction])に関する基本的なビジネスロジックを提供するサービスクラス
 * @param transactionRepository 取引履歴リポジトリ
 */
@Service
class TransactionService(private val transactionRepository: TransactionRepository) {

    /**
     * すべての取引履歴を取得します。
     * @return 取引履歴のリスト
     */
    fun findAllTransactions(): List<Transaction> {
        return transactionRepository.findAll()
    }

    /**
     * 指定された保有IDに紐づくすべての取引履歴を取得します。
     * @param holdingId 検索対象の保有ID
     * @return 見つかった取引履歴のリスト
     */
    fun findTransactionsByHoldingId(holdingId: Int): List<Transaction> {
        return transactionRepository.findByHoldingId(holdingId)
    }

    /**
     * 取引履歴を保存します。
     * @param transaction 保存する取引履歴オブジェクト
     * @return 保存された取引履歴オブジェクト
     */
    fun saveTransaction(transaction: Transaction): Transaction {
        return transactionRepository.save(transaction)
    }
}
