package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.StockLotRepository
import org.assertj.core.api.Assertions.assertThat
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
    fun `createStockLot should create a single lot with correct unit`() {
        // given
        val owner = Owner(id = 1, name = "Test Owner")
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", minimalUnit = 100)
        val unit = 2

        mockitoWhen(stockLotRepository.save(any(StockLot::class.java))).thenAnswer { it.getArgument(0) }

        // when
        val result = stockLotService.createStockLot(owner, stock, false, unit)

        // then
        verify(stockLotRepository).save(stockLotCaptor.capture())
        val capturedStockLot = stockLotCaptor.value

        assertThat(result).isNotNull
        assertThat(capturedStockLot.unit).isEqualTo(2)
        assertThat(capturedStockLot.owner).isEqualTo(owner)
        assertThat(capturedStockLot.stock).isEqualTo(stock)
    }

    @Test
    fun `findAllStockLots should return all lots`() {
        val lots = listOf(
            StockLot(id = 1, owner = Owner(1, "A"), stock = Stock(1, "1111", "S1", 100), unit = 1, isNisa = false),
            StockLot(id = 2, owner = Owner(2, "B"), stock = Stock(2, "2222", "S2", 100), unit = 2, isNisa = true)
        )
        mockitoWhen(stockLotRepository.findAll()).thenReturn(lots)
        val result = stockLotService.findAllStockLots()
        assertThat(result).hasSize(2).containsAll(lots)
    }

    @Test
    fun `findByOwnerId should return lots for owner`() {
        val owner = Owner(1, "A")
        val lots = listOf(
            StockLot(id = 1, owner = owner, stock = Stock(1, "1111", "S1", 100), unit = 1, isNisa = false)
        )
        mockitoWhen(stockLotRepository.findByOwnerId(1)).thenReturn(lots)
        val result = stockLotService.findByOwnerId(1)
        assertThat(result).hasSize(1).containsAll(lots)
    }

    @Test
    fun `findStockLotById should return lot if exists`() {
        val lot = StockLot(id = 1, owner = Owner(1, "A"), stock = Stock(1, "1111", "S1", 100), unit = 1, isNisa = false)
        mockitoWhen(stockLotRepository.findById(1)).thenReturn(java.util.Optional.of(lot))
        val result = stockLotService.findStockLotById(1)
        assertThat(result).isEqualTo(lot)
    }

    @Test
    fun `findStockLotById should throw if not exists`() {
        mockitoWhen(stockLotRepository.findById(1)).thenReturn(java.util.Optional.empty())
        org.junit.jupiter.api.Assertions.assertThrows(jakarta.persistence.EntityNotFoundException::class.java) {
            stockLotService.findStockLotById(1)
        }
    }
}
