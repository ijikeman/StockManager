package com.example.stock.service

import com.example.stock.dto.IncomingHistoryAddRequest
import com.example.stock.dto.IncomingHistoryDTO
import com.example.stock.dto.StockLotDTO
import com.example.stock.model.IncomingHistory
import com.example.stock.model.StockLot
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 入金履歴サービス
 */
@Service
@Transactional
class IncomingHistoryService(
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val stockLotRepository: StockLotRepository
) {

    /**
     * DTOに変換します
     */
    private fun StockLot.toDTO() = StockLotDTO(
        id = this.id,
        owner_id = this.owner.id,
        owner_name = this.owner.name,
        stock_code = this.stock.code,
        stock_name = this.stock.name,
        unit = this.unit,
        quantity = this.stock.minimalUnit * this.unit,
        is_nisa = this.isNisa,
        status = this.status,
        minimalUnit = this.stock.minimalUnit
    )

    /**
     * DTOに変換します
     */
    private fun IncomingHistory.toDTO() = IncomingHistoryDTO(
        id = this.id,
        lot_id = this.stockLot.id,
        incoming = this.incoming,
        payment_date = this.payment_date,
        stockLot = this.stockLot.toDTO()
    )

    /**
     * 全ての入金履歴を取得します
     */
    fun findAllIncomingHistories(): List<IncomingHistoryDTO> {
        return incomingHistoryRepository.findAll().map { it.toDTO() }
    }

    /**
     * IDで入金履歴を取得します
     */
    fun findIncomingHistoryById(id: Int): IncomingHistoryDTO {
        val incomingHistory = incomingHistoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("IncomingHistory not found with id: $id") }
        return incomingHistory.toDTO()
    }

    /**
     * 入金履歴を作成します
     */
    fun createIncomingHistory(request: IncomingHistoryAddRequest): IncomingHistoryDTO {
        val stockLot = stockLotRepository.findById(request.lot_id)
            .orElseThrow { EntityNotFoundException("StockLot not found with id: ${request.lot_id}") }

        val incomingHistory = IncomingHistory(
            stockLot = stockLot,
            incoming = request.incoming,
            payment_date = request.payment_date
        )

        val saved = incomingHistoryRepository.save(incomingHistory)
        return saved.toDTO()
    }

    /**
     * 入金履歴を更新します
     */
    fun updateIncomingHistory(id: Int, request: IncomingHistoryAddRequest): IncomingHistoryDTO {
        val incomingHistory = incomingHistoryRepository.findById(id)
            .orElseThrow { EntityNotFoundException("IncomingHistory not found with id: $id") }

        val stockLot = stockLotRepository.findById(request.lot_id)
            .orElseThrow { EntityNotFoundException("StockLot not found with id: ${request.lot_id}") }

        val updatedIncomingHistory = incomingHistory.copy(
            stockLot = stockLot,
            incoming = request.incoming,
            payment_date = request.payment_date
        )

        val saved = incomingHistoryRepository.save(updatedIncomingHistory)
        return saved.toDTO()
    }

    /**
     * 入金履歴を削除します
     */
    fun deleteIncomingHistory(id: Int) {
        if (!incomingHistoryRepository.existsById(id)) {
            throw EntityNotFoundException("IncomingHistory not found with id: $id")
        }
        incomingHistoryRepository.deleteById(id)
    }
}
