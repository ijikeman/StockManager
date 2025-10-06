package com.example.stock.repository

import com.example.stock.model.StockLot
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class StockLotRepositoryTest @Autowired constructor(
    val stockLotRepository: StockLotRepository,
    val ownerRepository: OwnerRepository,
    val stockRepository: StockRepository
) {
    @Test
    fun `save and find StockLot`() {
        // Arrange: 依存エンティティを保存
        val owner = ownerRepository.save(Owner(name = "Test Owner"))
        val stock = stockRepository.save(Stock(name = "Test Stock", code = "0000"))
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            unit = 100,
            isNisa = true
        )
        // Act: 保存
        val saved = stockLotRepository.save(stockLot)
        // Assert: 検索
        val found = stockLotRepository.findById(saved.id)
        assertEquals(true, found.isPresent)
        assertEquals(owner.id, found.get().owner.id)
        assertEquals(stock.id, found.get().stock.id)
        assertEquals(100, found.get().unit)
        assertEquals(true, found.get().isNisa)
    }
}
