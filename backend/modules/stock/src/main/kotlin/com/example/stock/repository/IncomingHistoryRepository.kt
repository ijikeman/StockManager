package com.example.stock.repository

import com.example.stock.model.IncomingHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IncomingHistoryRepository : JpaRepository<IncomingHistory, Int> {
    fun findByLotId(lotId: Int): List<IncomingHistory>
}
