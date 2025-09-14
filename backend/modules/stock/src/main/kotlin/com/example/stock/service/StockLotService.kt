package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository
) {
    fun createStockLot(owner: Owner, stock: Stock, isNisa: Boolean, quantity: Int): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            isNisa = isNisa,
            quantity = quantity
        )
        return stockLotRepository.save(stockLot)
    }
}
