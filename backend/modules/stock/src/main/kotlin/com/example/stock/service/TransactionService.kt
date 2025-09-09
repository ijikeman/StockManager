package com.example.stock.service

import com.example.stock.model.Transaction
import com.example.stock.repository.TransactionRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(private val transactionRepository: TransactionRepository) {

    fun findAllTransactions(): List<Transaction> {
        return transactionRepository.findAll()
    }

    fun findTransactionsByHoldingId(holdingId: Int): List<Transaction> {
        return transactionRepository.findByHoldingId(holdingId)
    }

    fun saveTransaction(transaction: Transaction): Transaction {
        return transactionRepository.save(transaction)
    }
}
