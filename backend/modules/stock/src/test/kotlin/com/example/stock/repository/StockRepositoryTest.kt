package com.example.stock.repository

import com.example.stock.model.Stock
import com.example.stock.model.Sector
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class StockRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var stockRepository: StockRepository

    // --- 正常系 ---
    // 正しくfindByIdできるか
    @Test
    fun `findById should return stock when stock exists`() {
        // given
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)
    val stock = Stock(code = "9999", name = "test stock", current_price = 1000.0, incoming = 10.0, earnings_date = java.time.LocalDate.of(2025, 1, 1), sector = persistedSector)
        // persistした結果、自動採番されたIDを含むインスタンスが返る
        val persistedStock = entityManager.persist(stock)
        entityManager.flush()

        // when
        val found = stockRepository.findById(persistedStock.id)

        // then
        assertThat(found).isNotNull
        assertEquals(found.get().name, "test stock")
    }

    // 正しくfindByCodeできるか
    @Test
    fun `findByCode should return stock when stock exists`() {
        // given
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)
    val stock = Stock(code = "9999", name = "test stock", current_price = 1000.0, incoming = 10.0, earnings_date = java.time.LocalDate.of(2025, 1, 1), sector = persistedSector)
        entityManager.persist(stock)
        entityManager.flush()

        // when
        val found = stockRepository.findByCode("9999")

        // then
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo(stock.name)
    }

    // save()で保存できるか
    @Test
    fun `save should persist stock`() {
        // given
        val sector = Sector(name = "test sector")
        val persistedSector = entityManager.persist(sector)
    val stock = Stock(code = "9998", name = "new stock", current_price = 2000.0, incoming = 20.0, earnings_date = java.time.LocalDate.of(2025, 1, 2), sector = persistedSector)

        // when
        val savedStock = stockRepository.save(stock)

        // then
        val foundInDb = entityManager.find(Stock::class.java, savedStock.id)
        assertThat(foundInDb).isNotNull
        assertThat(foundInDb.name).isEqualTo(stock.name)
    }

    // --- 異常系 ---
    // 存在しない銘柄を検索
    @Test
    fun `findByCode should return null when stock does not exist`() {
        // when
        val found = stockRepository.findByCode("0000")

        // then
        assertThat(found).isNull()
    }

    // 存在しないIDでfindByIdを実行する
    @Test
    fun `findById should return empty when stock does not exist`() {
        // when
        val found = stockRepository.findById(99999)

        // then
        assertThat(found).isNotPresent
    }
}
