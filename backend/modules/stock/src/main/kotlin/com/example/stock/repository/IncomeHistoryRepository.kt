package com.example.stock.repository

import com.example.stock.model.IncomeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomeHistoryRepository : JpaRepository<IncomeHistory, Int> {
    fun findByHoldingId(holdingId: Int): List<IncomeHistory>
}
