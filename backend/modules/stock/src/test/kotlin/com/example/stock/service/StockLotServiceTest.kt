package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.StockLotRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen
import org.mockito.ArgumentMatchers.any

@ExtendWith(MockitoExtension::class)
class StockLotServiceTest {

    @InjectMocks
    private lateinit var stockLotService: StockLotService

    @Mock
    private lateinit var stockLotRepository: StockLotRepository

    @Captor
    private lateinit var stockLotCaptor: ArgumentCaptor<StockLot>

    @Test
    fun `createStockLots should create lots with minimum unit`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimum_unit = 50)
        val totalQuantity = 200

        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = stockLotService.createStockLots(owner, stock, false, totalQuantity)

        // then
        verify(stockLotRepository, org.mockito.Mockito.times(4)).save(stockLotCaptor.capture())
        val capturedStockLots = stockLotCaptor.allValues

        assertThat(result).hasSize(4)
        assertThat(capturedStockLots.all { it.quantity == 50 }).isTrue()
    }

    @Test
    fun `createStockLots should throw exception for invalid quantity`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimum_unit = 50)
        val totalQuantity = 120

        // when & then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            stockLotService.createStockLots(owner, stock, false, totalQuantity)
        }
        assertThat(exception.message).isEqualTo("Quantity must be a multiple of 50")
    }

    @Test
    fun `createStockLots should handle zero minimum unit`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimum_unit = 0)
        val totalQuantity = 10

        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = stockLotService.createStockLots(owner, stock, false, totalQuantity)

        // then
        verify(stockLotRepository, org.mockito.Mockito.times(10)).save(stockLotCaptor.capture())
        val capturedStockLots = stockLotCaptor.allValues

        assertThat(result).hasSize(10)
        assertThat(capturedStockLots.all { it.quantity == 1 }).isTrue()
    }
}
