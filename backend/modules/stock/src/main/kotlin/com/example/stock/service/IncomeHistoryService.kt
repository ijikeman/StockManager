package com.example.stock.service

import com.example.stock.model.IncomeHistory
import com.example.stock.repository.IncomeHistoryRepository
import org.springframework.stereotype.Service

@Service
class IncomeHistoryService(private val incomeHistoryRepository: IncomeHistoryRepository) {

    fun findAllIncomeHistories(): List<IncomeHistory> {
        return incomeHistoryRepository.findAll()
    }

    fun findIncomeHistoriesByHoldingId(holdingId: Int): List<IncomeHistory> {
        return incomeHistoryRepository.findByHoldingId(holdingId)
    }

    fun saveIncomeHistory(incomeHistory: IncomeHistory): IncomeHistory {
        return incomeHistoryRepository.save(incomeHistory)
    }
}
