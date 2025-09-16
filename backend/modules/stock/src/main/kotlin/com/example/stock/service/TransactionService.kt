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
    // 取引（売買）に関するサービスクラス
    private val transactionRepository: TransactionRepository,
    private val stockLotRepository: StockLotRepository,
    private val stockRepository: StockRepository,
    private val ownerRepository: OwnerRepository,
    private val stockLotService: StockLotService
) {

    private fun Transaction.toDTO() = TransactionDTO(
        // EntityからDTOへの変換
        id = this.id,
        date = this.transaction_date,
        type = this.type.toString(),
        stock = StockInfoDTO(
            code = this.stockLot.stock.code,
            name = this.stockLot.stock.name
        ),
        owner_id = this.stockLot.owner.id,
        owner_name = this.stockLot.owner.name,
        unit = this.unit,
        price = this.price.toDouble(),
        fees = this.fee.toDouble(),
        lot_id = this.stockLot.id
    )

    fun findAllTransactions(): List<TransactionDTO> {
        // 全ての取引を取得
        return transactionRepository.findAll().map { it.toDTO() }
    }

    fun findTransactionById(id: Int): TransactionDTO {
        // IDで取引を取得（存在しなければ例外）
        return transactionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $id") }
            .toDTO()
    }

    fun createTransaction(request: TransactionAddRequest): List<TransactionDTO> {
        // 取引（売買）を新規作成
        val owner = ownerRepository.findById(request.owner_id)
            .orElseThrow { EntityNotFoundException("Owner not found with id: ${request.owner_id}") }
        val stock = stockRepository.findByCode(request.stock_code)
            ?: throw EntityNotFoundException("Stock not found with code: ${request.stock_code}")

        // 取引リスト（通常は1件のみ）

        val transactions = mutableListOf<Transaction>()

        if (request.type.equals("BUY", ignoreCase = true)) {
            // 買い注文の場合
            // 新しいロットを作成
            val stockLot = stockLotService.createStockLot(owner, stock, request.nisa, request.unit)
            val transaction = Transaction(
                stockLot = stockLot,
                type = TransactionType.BUY,
                unit = request.unit, // 取引の量は単元数
                price = request.price.toBigDecimal(),
                fee = request.fees.toBigDecimal(),
                transaction_date = request.date
            )
            // 取引を保存
            transactions.add(transactionRepository.save(transaction))
        } else {
            // 売り注文の場合、既存ロットを利用
            // For a SELL transaction, we use an existing lot.
            val lotId = request.lot_id ?: throw IllegalArgumentException("lot_id is required for SELL transactions")
            val stockLot = stockLotRepository.findById(lotId)
                .orElseThrow { EntityNotFoundException("StockLot not found with id: $lotId") }

            val transaction = Transaction(
                stockLot = stockLot,
                type = TransactionType.SELL,
                unit = request.unit,
                price = request.price.toBigDecimal(),
                fee = request.fees.toBigDecimal(),
                transaction_date = request.date
            )
            // 取引を保存
            transactions.add(transactionRepository.save(transaction))

            // Mark the lot as sold
            // ロットの状態をSOLDに更新
            val soldStockLot = stockLot.copy(status = com.example.stock.model.LotStatus.SOLD)
            stockLotRepository.save(soldStockLot)
        }

        // DTOリストで返却
        return transactions.map { it.toDTO() }
    }

    fun deleteTransaction(id: Int) {
        // 取引を削除
        if (!transactionRepository.existsById(id)) {
            throw EntityNotFoundException("Transaction not found with id: $id")
        }
        transactionRepository.deleteById(id)
    }
}
