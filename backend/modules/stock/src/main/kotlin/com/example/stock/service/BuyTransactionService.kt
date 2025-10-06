package com.example.stock.service

import com.example.stock.model.BuyTransaction
import com.example.stock.repository.BuyTransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * サービス: BuyTransaction（購入取引）
 */
@Service
@Transactional
class BuyTransactionService(
    private val buyTransactionRepository: BuyTransactionRepository
) {
    /**
     * 購入取引を作成します。
     * @param buyTransaction 作成する取引エンティティ
     * @return 保存された取引エンティティ
     */
    fun create(buyTransaction: BuyTransaction): BuyTransaction {
        return buyTransactionRepository.save(buyTransaction)
    }
}
