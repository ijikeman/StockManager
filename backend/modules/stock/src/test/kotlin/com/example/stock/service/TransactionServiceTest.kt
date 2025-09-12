package com.example.stock.service

import com.example.stock.dto.TransactionAddRequest
import com.example.stock.model.*
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen
import org.mockito.ArgumentMatchers.any
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {

    @InjectMocks
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var holdingRepository: HoldingRepository

    @Mock
    private lateinit var stockRepository: StockRepository

    @Test
    fun `createTransaction should create and return transaction DTO`() {
        // given
        val stockCode = "1234"
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "buy",
            stock_code = stockCode,
            quantity = 100,
            price = 500.0,
            fees = 10.0
        )

        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = stockCode, name = "Test Stock")
        val holding = Holding(id = 1, owner = owner, stock = stock, average_price = 450.0)

        mockitoWhen(stockRepository.findByCode(stockCode)).thenReturn(stock)
        mockitoWhen(holdingRepository.findByStock(stock)).thenReturn(holding)
        // When save is called, just return the argument that was passed to it
        mockitoWhen(transactionRepository.save(any(Transaction::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0)
        }

        // when
        val result = transactionService.createTransaction(request)

        // then
        assertThat(result).isNotNull
        assertThat(result.type).isEqualTo(request.type)
        assertThat(result.quantity).isEqualTo(request.quantity)
        assertThat(result.price).isEqualTo(request.price)
        assertThat(result.fees).isEqualTo(request.fees)
        assertThat(result.stock.code).isEqualTo(stockCode)
        assertThat(result.stock.name).isEqualTo("Test Stock")
    }
}
