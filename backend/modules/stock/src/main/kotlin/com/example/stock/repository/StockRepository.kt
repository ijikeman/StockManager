package com.example.stock.repository

import com.example.stock.model.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StockRepository : JpaRepository<Stock, Int> {
    // codeで検索
    fun findByCode(code: String): Stock?   
}
