package com.example.stock.service

import com.example.stock.model.Stock
import com.example.stock.model.Sector
import com.example.stock.repository.StockRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
// MockitoのwhenがKotlinのwhenキーワードと衝突するため、`mockitoWhen`という別名でインポートする
import org.mockito.Mockito.`when` as mockitoWhen
import java.util.Optional

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

    private val sector = Sector(id = 1, name = "Test Sector")

    @Test
    fun `findAll should return all stocks`() {
        // given: 前提条件の設定
        // モックのstockRepositoryがfindAll()メソッドを呼ばれた場合に、あらかじめ用意したstocksリストを返すように設定
        val stocks = listOf(Stock(id = 1, code = "1301", name = "stock1", current_price = 1000.0, dividend = 10.0, release_date = "20250101", sector = sector), Stock(id = 2, code = "1302", name = "stock2", current_price = 2000.0, dividend = 20.0, release_date = "20250202", sector = sector))
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
        val stock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, release_date = "20250101", sector = sector)
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
        val stock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, release_date = "20250101", sector = sector)
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
        val stockToSave = Stock(code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, release_date = "20250101", sector = sector)
        val savedStock = Stock(id = 1, code = "1301", name = "teststock", current_price = 1000.0, dividend = 10.0, release_date = "20250101", sector = sector)
        mockitoWhen(stockRepository.save(stockToSave)).thenReturn(savedStock)

        // when: 実際に保存処理を実行
        val result = stockService.save(stockToSave)

        // then: 返ってきたオブジェクトがnullでなく、IDが採番され、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(1)
        assertThat(result.code).isEqualTo("1301")
    }
}
