package com.example.stock.service

import com.example.stock.dto.StockInfoDTO
import com.example.stock.dto.TransactionAddRequest
import com.example.stock.dto.TransactionDTO
import com.example.stock.model.Transaction
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.TransactionRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val holdingRepository: HoldingRepository,
    private val stockRepository: StockRepository
) {

    private fun Transaction.toDTO() = TransactionDTO(
        id = this.id,
        date = this.date ?: throw IllegalStateException("Transaction date is null"),
        type = this.transaction_type,
        stock = StockInfoDTO(
            code = this.holding.stock.code,
            name = this.holding.stock.name
        ),
        quantity = this.volume,
        price = this.price,
        fees = this.tax
    )

    fun findAllTransactions(): List<TransactionDTO> {
        return transactionRepository.findAll().map { it.toDTO() }
    }

    fun findTransactionById(id: Int): TransactionDTO {
        return transactionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $id") }
            .toDTO()
    }

    fun createTransaction(request: TransactionAddRequest): TransactionDTO {
        val stock = stockRepository.findByCode(request.stock_code)
            ?: throw EntityNotFoundException("Stock not found with code: ${request.stock_code}")

        val holding = holdingRepository.findByStock(stock)
            ?: throw EntityNotFoundException("Holding not found for stock code: ${request.stock_code}")

        val transaction = Transaction(
            holding = holding,
            transaction_type = request.type,
            volume = request.quantity,
            price = request.price,
            tax = request.fees,
            date = request.date,
            average_price_at_transaction = holding.average_price
        )

        val savedTransaction = transactionRepository.save(transaction)
        return savedTransaction.toDTO()
    }

    fun updateTransaction(id: Int, request: TransactionAddRequest): TransactionDTO {
        val transactionToUpdate = transactionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $id") }

        val stock = stockRepository.findByCode(request.stock_code)
            ?: throw EntityNotFoundException("Stock not found with code: ${request.stock_code}")

        val holding = holdingRepository.findByStock(stock)
            ?: throw EntityNotFoundException("Holding not found for stock code: ${request.stock_code}")

        val updatedTransaction = transactionToUpdate.copy(
            holding = holding,
            transaction_type = request.type,
            volume = request.quantity,
            price = request.price,
            tax = request.fees,
            date = request.date,
            average_price_at_transaction = holding.average_price
        )

        val savedTransaction = transactionRepository.save(updatedTransaction)
        return savedTransaction.toDTO()
    }

    fun deleteTransaction(id: Int) {
        if (!transactionRepository.existsById(id)) {
            throw EntityNotFoundException("Transaction not found with id: $id")
        }
        transactionRepository.deleteById(id)
    }
}
