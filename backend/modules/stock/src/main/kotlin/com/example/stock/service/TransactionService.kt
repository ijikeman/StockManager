package com.example.stock.service

import com.example.stock.dto.StockInfoDTO
import com.example.stock.dto.TransactionAddRequest
import com.example.stock.dto.TransactionDTO
import com.example.stock.model.Holding
import com.example.stock.model.Transaction
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.OwnerRepository
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
    private val stockRepository: StockRepository,
    private val ownerRepository: OwnerRepository
) {

    /**
     * TransactionエンティティをTransactionDTOに変換します。
     * @return TransactionDTO
     * @throws IllegalStateException 取引日がない場合
     */
    private fun Transaction.toDTO() = TransactionDTO(
        id = this.id,
        date = this.date ?: throw IllegalStateException("Transaction date is null"),
        type = this.transaction_type,
        stock = StockInfoDTO(
            code = this.holding.stock.code,
            name = this.holding.stock.name
        ),
        owner_id = this.holding.owner.id,
        owner_name = this.holding.owner.name,
        quantity = this.volume,
        price = this.price,
        fees = this.tax
    )

    /**
     * すべての取引を取得します。
     * @return 取引DTOのリスト
     */
     fun findAllTransactions(): List<TransactionDTO> {
        return transactionRepository.findAll().map { it.toDTO() }
    }

    /**
     * IDで取引を検索します。
     * @param id 取引ID
     * @return 取引DTO
     * @throws EntityNotFoundException 指定されたIDの取引が見つからない場合
     */
    fun findTransactionById(id: Int): TransactionDTO {
        return transactionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $id") }
            .toDTO()
    }

    /**
     * 新しい取引を作成します。
     * @param request 取引追加リクエスト
     * @return 作成された取引DTO
     * @throws EntityNotFoundException 指定された銘柄コードまたは保有情報が見つからない場合
     */
    fun createTransaction(request: TransactionAddRequest): TransactionDTO {
        val holding = holdingRepository.findByStockCodeAndOwnerId(request.stock_code, request.owner_id)
            ?: run {
                val stock = stockRepository.findByCode(request.stock_code)
                    ?: throw EntityNotFoundException("Stock not found with code: ${request.stock_code}")
                val owner = ownerRepository.findById(request.owner_id)
                    .orElseThrow { EntityNotFoundException("Owner not found with id: ${request.owner_id}") }
                val newHolding = Holding(
                    owner = owner,
                    stock = stock,
                    nisa = false, // NISAフラグはリクエストに含まれていないため、デフォルトでfalseに設定
                    current_volume = 0,
                    average_price = 0.0
                )
                holdingRepository.save(newHolding)
            }

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

    /**
     * 取引を更新します。
     * @param id 取引ID
     * @param request 取引追加リクエスト
     * @return 更新された取引DTO
     * @throws EntityNotFoundException 指定されたIDの取引、銘柄コード、または保有情報が見つからない場合
     */
    fun updateTransaction(id: Int, request: TransactionAddRequest): TransactionDTO {
        val transactionToUpdate = transactionRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Transaction not found with id: $id") }

        val holding = holdingRepository.findByStockCodeAndOwnerId(request.stock_code, request.owner_id)
            ?: throw EntityNotFoundException("Holding not found for stock code: ${request.stock_code} and owner id: ${request.owner_id}")

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

    /**
     * 取引を削除します。
     * @param id 取引ID
     * @throws EntityNotFoundException 指定されたIDの取引が見つからない場合
     */
    fun deleteTransaction(id: Int) {
        if (!transactionRepository.existsById(id)) {
            throw EntityNotFoundException("Transaction not found with id: $id")
        }
        transactionRepository.deleteById(id)
    }
}
