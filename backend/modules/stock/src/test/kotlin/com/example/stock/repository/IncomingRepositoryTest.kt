package com.example.stock.repository

import com.example.stock.model.IncomingHistory
import com.example.stock.model.StockLot
import com.example.stock.model.SellTransaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
class IncomingHistoryRepositoryTest @Autowired constructor(
    val incomingHistoryRepository: IncomingHistoryRepository,
    val stockLotRepository: StockLotRepository,
    val sellTransactionRepository: SellTransactionRepository
) {
    @Test
    fun `save and find IncomingHistory`() {
        // Arrange: 依存エンティティを保存
        val stockLot = stockLotRepository.save(StockLot(/* 必要なフィールドを埋めてください */))
        val sellTransaction = sellTransactionRepository.save(SellTransaction(/* 必要なフィールドを埋めてください */))
        val incomingHistory = IncomingHistory(
            stockLot = stockLot,
            sellTransaction = sellTransaction,
            incoming = BigDecimal(10000),
            payment_date = LocalDate.now()
        )
        // Act: 保存
        val saved = incomingHistoryRepository.save(incomingHistory)
        // Assert: 検索
        val found = incomingHistoryRepository.findById(saved.id)
        assertEquals(true, found.isPresent)
        assertEquals(stockLot.id, found.get().stockLot.id)
        assertEquals(sellTransaction.id, found.get().sellTransaction.id)
        assertEquals(BigDecimal(10000), found.get().incoming)
    }
}
