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
    private lateinit var lotCaptor: ArgumentCaptor<StockLot>

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
        verify(stockLotRepository).save(lotCaptor.capture())
        val capturedLot = lotCaptor.value

        assertThat(result).isNotNull
        assertThat(capturedLot.unit).isEqualTo(2)
        assertThat(capturedLot.owner).isEqualTo(owner)
        assertThat(capturedLot.stock).isEqualTo(stock)
    }
}
