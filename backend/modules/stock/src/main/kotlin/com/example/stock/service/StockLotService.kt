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
    fun createStockLots(owner: Owner, stock: Stock, isNisa: Boolean, totalQuantity: Int): List<StockLot> {
        if (totalQuantity % 100 != 0) {
            throw IllegalArgumentException("Quantity must be a multiple of 100")
        }
        val lots = mutableListOf<StockLot>()
        for (i in 0 until totalQuantity / 100) {
            val stockLot = StockLot(
                owner = owner,
                stock = stock,
                isNisa = isNisa,
                quantity = 100
            )
            lots.add(stockLotRepository.save(stockLot))
        }
        return lots
    }

    fun createSingleStockLot(owner: Owner, stock: Stock, isNisa: Boolean, quantity: Int): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            isNisa = isNisa,
            quantity = quantity
        )
        return stockLotRepository.save(stockLot)
    }
}
