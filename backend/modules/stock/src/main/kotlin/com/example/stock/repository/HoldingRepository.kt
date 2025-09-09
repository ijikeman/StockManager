package com.example.stock.repository

import com.example.stock.model.Holding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HoldingRepository : JpaRepository<Holding, Int> {
    // owner_idで検索
    fun findByOwnerId(owner_id: Int): List<Holding>
}
