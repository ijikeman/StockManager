package com.example.stock.repository

import com.example.stock.model.SellTransaction
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
class SellTransactionRepositoryTest @Autowired constructor(
    val sellTransactionRepository: SellTransactionRepository,
    val buyTransactionRepository: BuyTransactionRepository,
    val ownerRepository: OwnerRepository,
    val stockRepository: StockRepository
) {
    @Test
    fun `save and find SellTransaction`() {
        // Arrange: 依存エンティティを保存
        val owner = ownerRepository.save(Owner(name = "Test Owner"))
        val stock = stockRepository.save(Stock(name = "Test Stock", code = "0000"))
        val buyTransaction = buyTransactionRepository.save(
            BuyTransaction(
                owner = owner,
                stock = stock,
                unit = 10,
                price = BigDecimal(1000),
                fee = BigDecimal(10),
                transaction_date = LocalDate.now()
            )
        )
        val sellTransaction = SellTransaction(
            buyTransaction = buyTransaction,
            unit = 5,
            price = BigDecimal(1200),
            fee = BigDecimal(8),
            transaction_date = LocalDate.now()
        )
        // Act: 保存
        val saved = sellTransactionRepository.save(sellTransaction)
        // Assert: 検索
        val found = sellTransactionRepository.findById(saved.id)
        assertEquals(true, found.isPresent)
        assertEquals(buyTransaction.id, found.get().buyTransaction.id)
        assertEquals(5, found.get().unit)
        assertEquals(BigDecimal(1200), found.get().price)
    }
}
