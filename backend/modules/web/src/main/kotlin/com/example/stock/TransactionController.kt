package com.example.stock

import com.example.stock.dto.TransactionAddRequest
import com.example.stock.dto.TransactionDTO
import com.example.stock.service.TransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * 取引関連のAPIを提供するコントローラー
 */
@RestController
@RequestMapping("/api/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {

    /**
     * 全ての取引履歴を取得します。
     * @return 取引履歴のリスト
     */
    @GetMapping
    fun getTransactions(): List<TransactionDTO> {
        return transactionService.findAllTransactions()
    }

    /**
     * 指定されたIDの取引履歴を取得します。
     * @param id 取引ID
     * @return 取引履歴
     */
    @GetMapping("/{id}")
    fun getTransaction(@PathVariable id: Int): ResponseEntity<TransactionDTO> {
        val transaction = transactionService.findTransactionById(id)
        return ResponseEntity.ok(transaction)
    }

    /**
     * 新しい取引を作成します。
     * @param request 取引作成リクエスト
     * @return 作成された取引履歴
     */
    @PostMapping
    fun createTransaction(@Validated @RequestBody request: TransactionAddRequest): ResponseEntity<List<TransactionDTO>> {
        val createdTransactions = transactionService.createTransaction(request)
        return ResponseEntity(createdTransactions, HttpStatus.CREATED)
    }

    /**
     * 指定されたIDの取引履歴を削除します。
     * @param id 取引ID
     * @return レスポンスエンティティ
     */
    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Int): ResponseEntity<Void> {
        transactionService.deleteTransaction(id)
        return ResponseEntity.noContent().build()
    }
}