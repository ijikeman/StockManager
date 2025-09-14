package com.example.stock.repository

import com.example.stock.model.BenefitHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BenefitHistoryRepository : JpaRepository<BenefitHistory, Int> {
    fun findByStockLotId(stockLotId: Int): List<BenefitHistory>
}
