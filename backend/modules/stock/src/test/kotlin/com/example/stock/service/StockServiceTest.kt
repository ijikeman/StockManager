package com.example.stock.service

import com.example.stock.model.Stock
import com.example.stock.model.Sector
import com.example.stock.repository.StockRepository
import com.example.stock.provider.YahooFinanceProvider
import com.example.stock.provider.StockInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
// MockitoのwhenがKotlinのwhenキーワードと衝突するため、`mockitoWhen`という別名でインポートする
import org.mockito.Mockito.`when` as mockitoWhen
import java.util.Optional
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * StockServiceの単体テストクラス。
 * Mockitoフレームワークを利用して、Repository層をモック化し、Service層のロジックのみをテストする。
 */
@ExtendWith(MockitoExtension::class) // JUnit 5でMockitoのアノテーション(@Mock, @InjectMocks)を有効にする
class StockServiceTest {

    @InjectMocks // テスト対象のクラス。@Mockアノテーションが付いたモックを自動的に注入する。
    private lateinit var stockService: StockService

    @Mock // モック(偽のオブジェクト)を作成する。ここではDBとのやり取りをシミュレートする。
    private lateinit var stockRepository: StockRepository

    @Mock
    private lateinit var yahooFinanceProvider: YahooFinanceProvider

    private val sector = Sector(id = 1, name = "Test Sector")

    @Test
    fun `findAll should return all stocks`() {
        // given: 前提条件の設定
        // モックのstockRepositoryがfindAll()メソッドを呼ばれた場合に、あらかじめ用意したstocksリストを返すように設定
        val stocks = listOf(Stock(id = 1, code = "1301", name = "stock1", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector), Stock(id = 2, code = "1302", name = "stock2", current_price = 2000.0, dividend = 20.0, earnings_date = "2025-02-02", sector = sector))
        mockitoWhen(stockRepository.findAll()).thenReturn(stocks) // stockRepository.findAll()を読んだときはstocksを返す

        // when: テスト対象のメソッドを実行
        val result = stockService.findAll()

        // then: 結果の検証
        // 結果の件数が2件であること、そして中身が準備したリストと一致することを確認
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(stocks)
    }

    @Test
    fun `findByCode should return stock when found`() {
        // given: "1301"というコードで検索されたら、特定のstockオブジェクトを返すように設定
        val stock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        mockitoWhen(stockRepository.findByCode("1301")).thenReturn(stock)

        // when: 実際に"1301"で検索を実行
        val result = stockService.findByCode("1301")

        // then: 返ってきたオブジェクトがnullでなく、コードが正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.code).isEqualTo("1301")
    }

    @Test
    fun `findByCode should return null when not found`() {
        // given: "unknown"というコードで検索されたら、nullを返すように設定
        mockitoWhen(stockRepository.findByCode("unknown")).thenReturn(null)

        // when: 実際に"unknown"で検索を実行
        val result = stockService.findByCode("unknown")

        // then: 結果がnullであることを確認
        assertThat(result).isNull()
    }

    @Test
    fun `findById should return stock when found`() {
        // given: IDが1で検索されたら、Optionalに包まれたstockオブジェクトを返すように設定
        val stock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        mockitoWhen(stockRepository.findById(1)).thenReturn(Optional.of(stock))

        // when: 実際にID=1で検索を実行
        val result = stockService.findById(1)

        // then: 返ってきたオブジェクトがnullでなく、IDが正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1)
    }

    @Test
    fun `findById should return null when not found`() {
        // given: IDが99で検索されたら、空のOptionalを返すように設定
        mockitoWhen(stockRepository.findById(99)).thenReturn(Optional.empty())

        // when: 実際にID=99で検索を実行
        val result = stockService.findById(99)

        // then: 結果がnullであることを確認 (Service層でOptional.empty()がnullに変換される)
        assertThat(result).isNull()
    }

    @Test
    fun `save should return saved stock`() {
        // given: 特定のstockオブジェクトを保存しようとしたら、IDが採番されたstockオブジェクトを返すように設定
        val stockToSave = Stock(code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        val savedStock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        mockitoWhen(stockRepository.save(stockToSave)).thenReturn(savedStock)

        // when: 実際に保存処理を実行
        val result = stockService.save(stockToSave)

        // then: 返ってきたオブジェクトがnullでなく、IDが採番され、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(1)
        assertThat(result.code).isEqualTo("1301")
    }

    @Test
    fun `updateStockPrice should update and return stock when found`() {
        // given
        val code = "1301"
        val originalStock = Stock(id = 1, code = code, name = "teststock", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        val stockInfo = StockInfo(price = 1200.0, dividend = 12.0, earnings_date = LocalDate.of(2025, 1, 2))
        val updatedStock = originalStock.copy(current_price = 1200.0, dividend = 12.0, earnings_date = "2025-01-02")

        mockitoWhen(yahooFinanceProvider.fetchStockInfo(code)).thenReturn(stockInfo)
        mockitoWhen(stockRepository.findByCode(code)).thenReturn(originalStock)
        mockitoWhen(stockRepository.save(updatedStock)).thenReturn(updatedStock)

        // when
        val result = stockService.updateStockPrice(code)

        // then
        assertThat(result).isNotNull
        assertThat(result?.current_price).isEqualTo(1200.0)
        assertThat(result?.dividend).isEqualTo(12.0)
        assertThat(result?.earnings_date).isEqualTo("2025-01-02")
    }

    @Test
    fun `updateStockPrice should return null when stock not in db`() {
        // given
        val code = "1301"
        val stockInfo = StockInfo(price = 1200.0, dividend = 12.0, earnings_date = LocalDate.of(2025, 1, 2))

        mockitoWhen(yahooFinanceProvider.fetchStockInfo(code)).thenReturn(stockInfo)
        mockitoWhen(stockRepository.findByCode(code)).thenReturn(null)

        // when
        val result = stockService.updateStockPrice(code)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `updateStockPrice should return null when provider fails`() {
        // given
        val code = "1301"
        mockitoWhen(yahooFinanceProvider.fetchStockInfo(code)).thenReturn(null)

        // when
        val result = stockService.updateStockPrice(code)

        // then
        assertThat(result).isNull()
    }

    @Test
    fun `updateAllStockPrices should update all stocks`() {
        // given
        val stock1 = Stock(id = 1, code = "1301", name = "stock1", current_price = 1000.0, dividend = 10.0, earnings_date = "2025-01-01", sector = sector)
        val stock2 = Stock(id = 2, code = "1302", name = "stock2", current_price = 2000.0, dividend = 20.0, earnings_date = "2025-02-02", sector = sector)
        val stocks = listOf(stock1, stock2)

        val stockInfo1 = StockInfo(price = 1100.0, dividend = 11.0, earnings_date = LocalDate.of(2025, 1, 11))
        val stockInfo2 = StockInfo(price = 2200.0, dividend = 22.0, earnings_date = LocalDate.of(2025, 2, 12))

        val updatedStock1 = stock1.copy(current_price = 1100.0, dividend = 11.0, earnings_date = "2025-01-11")
        val updatedStock2 = stock2.copy(current_price = 2200.0, dividend = 22.0, earnings_date = "2025-02-12")

        mockitoWhen(stockRepository.findAll()).thenReturn(stocks)
        mockitoWhen(yahooFinanceProvider.fetchStockInfo("1301")).thenReturn(stockInfo1)
        mockitoWhen(yahooFinanceProvider.fetchStockInfo("1302")).thenReturn(stockInfo2)
        mockitoWhen(stockRepository.findByCode("1301")).thenReturn(stock1)
        mockitoWhen(stockRepository.findByCode("1302")).thenReturn(stock2)
        mockitoWhen(stockRepository.save(updatedStock1)).thenReturn(updatedStock1)
        mockitoWhen(stockRepository.save(updatedStock2)).thenReturn(updatedStock2)

        // when
        val result = stockService.updateAllStockPrices()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].current_price).isEqualTo(1100.0)
        assertThat(result[1].current_price).isEqualTo(2200.0)
    }
}
