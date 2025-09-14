package com.example.stock.service

import com.example.stock.dto.IncomingHistoryAddRequest
import com.example.stock.dto.IncomingHistoryDTO
import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class IncomingHistoryService(
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val stockLotRepository: StockLotRepository
) {

    private fun IncomingHistory.toDTO() = IncomingHistoryDTO(
        id = this.id,
        lot_id = this.stockLot.id,
        incoming = this.incoming,
        payment_date = this.payment_date
    )

    fun findAllIncomingHistories(): List<IncomingHistoryDTO> {
        return incomingHistoryRepository.findAll().map { it.toDTO() }
    }

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
}
