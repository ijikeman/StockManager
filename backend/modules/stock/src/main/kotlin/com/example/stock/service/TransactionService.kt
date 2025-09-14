package com.example.stock.service

import com.example.stock.dto.StockInfoDTO
import com.example.stock.dto.TransactionAddRequest
import com.example.stock.dto.TransactionDTO
import com.example.stock.model.Transaction
import com.example.stock.model.TransactionType
import com.example.stock.repository.OwnerRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.TransactionRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val stockLotRepository: StockLotRepository,
    private val stockRepository: StockRepository,
    private val ownerRepository: OwnerRepository,
    private val stockLotService: StockLotService
) {

    private fun Transaction.toDTO() = TransactionDTO(
        id = this.id,
        date = this.transaction_date,
        type = this.type.toString(),
        stock = StockInfoDTO(
            code = this.stockLot.stock.code,
            name = this.stockLot.stock.name
        ),
        owner_id = this.stockLot.owner.id,
        owner_name = this.stockLot.owner.name,
        quantity = this.quantity,
        price = this.price.toDouble(),
        fees = this.tax.toDouble()
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
        val owner = ownerRepository.findById(request.owner_id)
            .orElseThrow { EntityNotFoundException("Owner not found with id: ${request.owner_id}") }
        val stock = stockRepository.findByCode(request.stock_code)
            ?: throw EntityNotFoundException("Stock not found with code: ${request.stock_code}")

        val transaction: Transaction = if (request.type.equals("BUY", ignoreCase = true)) {
            // For a BUY transaction, we create a new lot.
            val stockLot = stockLotService.createStockLot(owner, stock, request.nisa, request.quantity)
            Transaction(
                stockLot = stockLot,
                type = TransactionType.BUY,
                quantity = request.quantity,
                price = request.price.toBigDecimal(),
                tax = request.fees.toBigDecimal(),
                transaction_date = request.date
            )
        } else {
            // For a SELL transaction, we use an existing lot.
            val lotId = request.lot_id ?: throw IllegalArgumentException("lot_id is required for SELL transactions")
            val stockLot = stockLotRepository.findById(lotId)
                .orElseThrow { EntityNotFoundException("StockLot not found with id: $lotId") }

            // Here you might want to add logic to check if the lot has enough quantity to sell.
            // For now, we just create the transaction.

            Transaction(
                stockLot = stockLot,
                type = TransactionType.SELL,
                quantity = request.quantity,
                price = request.price.toBigDecimal(),
                tax = request.fees.toBigDecimal(),
                transaction_date = request.date
            )
        }

        val savedTransaction = transactionRepository.save(transaction)
        return savedTransaction.toDTO()
    }

    fun deleteTransaction(id: Int) {
        if (!transactionRepository.existsById(id)) {
            throw EntityNotFoundException("Transaction not found with id: $id")
        }
        transactionRepository.deleteById(id)
    }
}
