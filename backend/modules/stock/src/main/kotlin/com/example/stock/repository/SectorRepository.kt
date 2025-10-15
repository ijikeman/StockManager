package com.example.stock.repository

import com.example.stock.model.Sector
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorRepository : JpaRepository<Sector, Int> {
    /* 名前からセクターを検索 */
    fun findByName(name: String): Sector?
}
