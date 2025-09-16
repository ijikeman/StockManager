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
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
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

    @Test
    fun `createTransaction for BUY should create a new lot and a transaction`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
    val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "BUY",
            stock_code = stock.code,
            owner_id = owner.id,
            unit = 2,
            price = 500.0,
            fees = 10.0,
            nisa = true
        )
        val newStockLot = StockLot(id = 1, owner = owner, stock = stock, unit = 2, isNisa = true)
        val transactionCaptor = argumentCaptor<Transaction>()
        val unitCaptor = argumentCaptor<Int>()

        whenever(ownerRepository.findById(owner.id)).thenReturn(Optional.of(owner))
        whenever(stockRepository.findByCode(stock.code)).thenReturn(stock)
        whenever(stockLotService.createStockLot(any(), any(), any(), any())).thenReturn(newStockLot)
        whenever(transactionRepository.save(any())).thenAnswer { it.getArgument(0) }

        // when
        val result = transactionService.createTransaction(request)

        // then
        verify(stockLotService).createStockLot(any(), any(), any(), unitCaptor.capture())
        assertThat(unitCaptor.firstValue).isEqualTo(2)

        verify(transactionRepository).save(transactionCaptor.capture())
        val capturedTransaction = transactionCaptor.firstValue

        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo("BUY")
        assertThat(result[0].unit).isEqualTo(2)
        assertThat(capturedTransaction.stockLot).isEqualTo(newStockLot)
    }

    @Test
    fun `createTransaction for SELL should use existing lot and create transaction`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
        val stockLot = StockLot(id = 1, owner = owner, stock = stock, unit = 1, isNisa = true)
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "SELL",
            stock_code = stock.code,
            owner_id = owner.id,
            unit = 1,
            price = 600.0,
            fees = 15.0,
            lot_id = stockLot.id
        )
        val transactionCaptor = argumentCaptor<Transaction>()

        whenever(ownerRepository.findById(owner.id)).thenReturn(Optional.of(owner))
        whenever(stockRepository.findByCode(stock.code)).thenReturn(stock)
        whenever(stockLotRepository.findById(stockLot.id)).thenReturn(Optional.of(stockLot))
        whenever(transactionRepository.save(any())).thenAnswer { it.getArgument(0) }
        whenever(stockLotRepository.save(any())).thenAnswer { it.getArgument(0) }

        // when
        val result = transactionService.createTransaction(request)

        // then
        verify(transactionRepository).save(transactionCaptor.capture())
        val capturedTransaction = transactionCaptor.firstValue

        assertThat(result).hasSize(1)
        assertThat(result[0].type).isEqualTo("SELL")
        assertThat(result[0].unit).isEqualTo(1)
        assertThat(capturedTransaction.stockLot).isEqualTo(stockLot)
    }
}
