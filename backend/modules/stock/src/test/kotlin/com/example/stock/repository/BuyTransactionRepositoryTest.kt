package com.example.stock.repository

import com.example.stock.model.BuyTransaction
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
class BuyTransactionRepositoryTest @Autowired constructor(
    val buyTransactionRepository: BuyTransactionRepository,
    val ownerRepository: OwnerRepository,
    val stockRepository: StockRepository
) {
    @Test
    fun `save and find BuyTransaction`() {
        // Arrange: 依存エンティティを保存
        val owner = ownerRepository.save(Owner(name = "Test Owner"))
        val stock = stockRepository.save(Stock(name = "Test Stock", code = "0000"))
        val buyTransaction = BuyTransaction(
            owner = owner,
            stock = stock,
            unit = 10,
            price = BigDecimal(1000),
            fee = BigDecimal(10),
            transaction_date = LocalDate.now()
        )
        // Act: 保存
        val saved = buyTransactionRepository.save(buyTransaction)
        // Assert: 検索
        val found = buyTransactionRepository.findById(saved.id)
        assertEquals(true, found.isPresent)
        assertEquals(owner.id, found.get().owner.id)
        assertEquals(stock.id, found.get().stock.id)
        assertEquals(10, found.get().unit)
    }
}
