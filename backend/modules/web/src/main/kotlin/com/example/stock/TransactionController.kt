package com.example.stock

import com.example.stock.dto.TransactionAddRequest
import com.example.stock.dto.TransactionDTO
import com.example.stock.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {

    @GetMapping
    fun getTransactions(): List<TransactionDTO> {
        return transactionService.findAllTransactions()
    }

    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: Int): ResponseEntity<TransactionDTO> {
        val transaction = transactionService.findTransactionById(id)
        return ResponseEntity.ok(transaction)
    }

    @PostMapping
    fun createTransaction(@Validated @RequestBody request: TransactionAddRequest): ResponseEntity<TransactionDTO> {
        val createdTransaction = transactionService.createTransaction(request)
        return ResponseEntity(createdTransaction, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Int): ResponseEntity<Void> {
        transactionService.deleteTransaction(id)
        return ResponseEntity.noContent().build()
    }
}
