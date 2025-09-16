package com.example.stock.service

import com.example.stock.dto.IncomingHistoryAddRequest
import com.example.stock.dto.IncomingHistoryDTO
import com.example.stock.model.IncomingHistory
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
    private fun IncomingHistory.toDTO() = IncomingHistoryDTO(
        id = this.id,
        lot_id = this.lot.id,
        incoming = this.incoming,
        payment_date = this.payment_date
    )

    /**
     * 全ての入金履歴を取得します
     */
    fun findAllIncomingHistories(): List<IncomingHistoryDTO> {
        // データベースからすべての`IncomingHistory`エンティティを取得し、それぞれを`IncomingHistoryDTO`に変換したリストを返す
        return incomingHistoryRepository.findAll().map { it.toDTO() }
    }

    /**
     * 入金履歴を作成します
     */
    fun createIncomingHistory(request: IncomingHistoryAddRequest): IncomingHistoryDTO {
        val lot = stockLotRepository.findById(request.lot_id)
            .orElseThrow { EntityNotFoundException("StockLot not found with id: ${request.lot_id}") }

        val incomingHistory = IncomingHistory(
            lot = lot,
            incoming = request.incoming,
            payment_date = request.payment_date
        )

        val saved = incomingHistoryRepository.save(incomingHistory)
        return saved.toDTO()
    }
}
