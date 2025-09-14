package com.example.stock.service

import com.example.stock.dto.TransactionAddRequest
import com.example.stock.model.*
import com.example.stock.repository.OwnerRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen
import org.mockito.Mockito.verify
import org.mockito.ArgumentMatchers.any
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {

    @InjectMocks
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var stockLotRepository: StockLotRepository

    @Mock
    private lateinit var stockRepository: StockRepository

    @Mock
    private lateinit var ownerRepository: OwnerRepository

    @Mock
    private lateinit var stockLotService: StockLotService

    @Captor
    private lateinit var transactionCaptor: ArgumentCaptor<Transaction>

    @Test
    fun `createTransaction for BUY should create new lots and transactions`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock")
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "BUY",
            stock_code = stock.code,
            owner_id = owner.id,
            quantity = 200, // 2 lots of 100
            price = 500.0,
            fees = 10.0,
            nisa = true
        )

        val stockLots = listOf(
            StockLot(id = 1, owner = owner, stock = stock, quantity = 100, isNisa = true),
            StockLot(id = 2, owner = owner, stock = stock, quantity = 100, isNisa = true)
        )

        mockitoWhen(ownerRepository.findById(owner.id)).thenReturn(Optional.of(owner))
        mockitoWhen(stockRepository.findByCode(stock.code)).thenReturn(stock)
        mockitoWhen(stockLotService.createStockLots(owner, stock, request.nisa, request.quantity)).thenReturn(stockLots)
        mockitoWhen(transactionRepository.save(any(Transaction::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = transactionService.createTransaction(request)

        // then
        verify(transactionRepository, org.mockito.Mockito.times(2)).save(transactionCaptor.capture())
        val capturedTransactions = transactionCaptor.allValues

        assertThat(result).hasSize(2)
        assertThat(result[0].type).isEqualTo("BUY")
        assertThat(result[0].quantity).isEqualTo(100)
        assertThat(capturedTransactions[0].stockLot).isEqualTo(stockLots[0])

        assertThat(result[1].type).isEqualTo("BUY")
        assertThat(result[1].quantity).isEqualTo(100)
        assertThat(capturedTransactions[1].stockLot).isEqualTo(stockLots[1])
    }

    @Test
    fun `createTransaction for SELL should use existing lot and create transaction`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock")
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, quantity = 100, isNisa = true)
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "SELL",
            stock_code = stock.code,
            owner_id = owner.id,
            quantity = 50,
            price = 600.0,
            fees = 15.0,
            lot_id = stockLot.id
        )

        mockitoWhen(ownerRepository.findById(owner.id)).thenReturn(Optional.of(owner))
        mockitoWhen(stockRepository.findByCode(stock.code)).thenReturn(stock)
        mockitoWhen(stockLotRepository.findById(stockLot.id)).thenReturn(Optional.of(stockLot))
        mockitoWhen(transactionRepository.save(any(Transaction::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = transactionService.createTransaction(request)

        // then
        verify(transactionRepository).save(transactionCaptor.capture())
        val capturedTransaction = transactionCaptor.value

        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo("SELL")
        assertThat(result[0].quantity).isEqualTo(50)
        assertThat(capturedTransaction.stockLot).isEqualTo(stockLot)
    }
}
