package com.example.stock.service

import com.example.stock.dto.TransactionAddRequest
import com.example.stock.model.*
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.OwnerRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito.verify
import java.util.Optional
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

    @Mock
    private lateinit var ownerRepository: OwnerRepository

    @Captor
    private lateinit var transactionCaptor: ArgumentCaptor<Transaction>

    @Captor
    private lateinit var holdingCaptor: ArgumentCaptor<Holding>

    @Test
    fun `createTransaction should create and return transaction DTO`() {
        // given
        val stockCode = "1234"
        val owner = Owner(id = 1, name = "Test Owner")
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "buy",
            stock_code = stockCode,
            owner_id = owner.id,
            quantity = 100,
            price = 500.0,
            fees = 10.0,
            nisa = true
        )

        val stock = Stock(id = 1, code = stockCode, name = "Test Stock")
        val holding = Holding(id = 1, owner = owner, stock = stock, average_price = 450.0)

        mockitoWhen(holdingRepository.findByStockCodeAndOwnerId(stockCode, owner.id)).thenReturn(holding)
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

    @Test
    fun `createTransaction should create new holding when holding not found`() {
        // given
        val stockCode = "5678"
        val ownerId = 2
        val owner = Owner(id = ownerId, name = "Another Owner")
        val request = TransactionAddRequest(
            date = LocalDate.now(),
            type = "buy",
            stock_code = stockCode,
            owner_id = ownerId,
            quantity = 50,
            price = 1000.0,
            fees = 20.0,
            nisa = true
        )

        val stock = Stock(id = 2, code = stockCode, name = "Another Stock")
        val newHolding = Holding(id = 2, owner = owner, stock = stock, current_volume = 0, average_price = 0.0)

        mockitoWhen(holdingRepository.findByStockCodeAndOwnerId(stockCode, ownerId)).thenReturn(null)
        mockitoWhen(stockRepository.findByCode(stockCode)).thenReturn(stock)
        mockitoWhen(ownerRepository.findById(ownerId)).thenReturn(Optional.of(owner))
        mockitoWhen(holdingRepository.save(any(Holding::class.java))).thenReturn(newHolding)
        mockitoWhen(transactionRepository.save(any(Transaction::class.java))).thenAnswer { invocation ->
            invocation.getArgument(0)
        }

        // when
        val result = transactionService.createTransaction(request)

        // then
        verify(holdingRepository).save(holdingCaptor.capture())
        verify(transactionRepository).save(transactionCaptor.capture())
        val capturedTransaction = transactionCaptor.value
        val capturedHolding = holdingCaptor.value

        // Assert DTO properties
        assertThat(result).isNotNull
        assertThat(result.stock.code).isEqualTo(stockCode)
        assertThat(result.owner_id).isEqualTo(ownerId)
        assertThat(result.quantity).isEqualTo(request.quantity)

        // Assert captured Transaction entity properties
        assertThat(capturedTransaction.holding).isNotNull
        assertThat(capturedTransaction.holding.id).isEqualTo(newHolding.id)
        assertThat(capturedTransaction.holding.owner.id).isEqualTo(ownerId)
        assertThat(capturedTransaction.holding.stock.code).isEqualTo(stockCode)
        assertThat(capturedTransaction.volume).isEqualTo(request.quantity)

        // Assert captured Holding entity properties
        assertThat(capturedHolding.nisa).isTrue()
    }
}
