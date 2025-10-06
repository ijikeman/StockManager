package com.example.stock.repository

import com.example.stock.model.Owner
import com.example.stock.model.Sector
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager


@DataJpaTest
class StockLotRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var stockLotRepository: StockLotRepository

    // --- 正常系 ---
    // 正しくfindByIdできるか
    @Test
    fun `find StockLot by id`() {
        // Arrange
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)

        val stock = Stock(
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = persistedSector
        )
        val persistedStock = entityManager.persist(stock)

        val owner = Owner(name = "testuser")
        val persistedOwner = entityManager.persist(owner)

        val stockLot = StockLot(
            owner = persistedOwner,
            stock = persistedStock,
            currentUnit = 100
        )
        val persistedStockLot = entityManager.persist(stockLot)

        // Act
        val found = stockLotRepository.findById(persistedStockLot.id)

        // Assert
        assertEquals(true, found.isPresent)
        assertEquals(persistedOwner.id, found.get().owner.id)
        assertEquals(persistedStock.id, found.get().stock.id)
        assertEquals(100, found.get().currentUnit)
    }

    // 正しく保存できるか
    @Test
    fun `save and find StockLot`() {
        // Arrange: 依存エンティティを保存
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)

        val stock = Stock(
            code = "9999",
            name = "test stock",
            currentPrice = 1000.0,
            incoming = 10.0,
            earningsDate = java.time.LocalDate.of(2025, 1, 1),
            sector = persistedSector)
        val persistedStock = entityManager.persist(stock)

        val owner = Owner(name = "testuser")
        val persistedOwner = entityManager.persist(owner)
    
        // StockLotの作成
        val stockLot = StockLot(
            owner = persistedOwner,
            stock = persistedStock,
            currentUnit = 100,
        )
        // Act: 保存
        val saved = stockLotRepository.save(stockLot)
        // Assert: 検索
        val found = stockLotRepository.findById(saved.id)
        assertEquals(true, found.isPresent)
        assertEquals(owner.id, found.get().owner.id)
        assertEquals(stock.id, found.get().stock.id)
        assertEquals(100, found.get().currentUnit)
    }
}
