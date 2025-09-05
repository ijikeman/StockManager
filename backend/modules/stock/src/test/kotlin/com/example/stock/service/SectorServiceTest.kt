package com.example.stock.service

import com.example.stock.model.Sector
import com.example.stock.repository.SectorRepository
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
 * SectorServiceの単体テストクラス。
 * Mockitoフレームワークを利用して、Repository層をモック化し、Service層のロジックのみをテストする。
 */
@ExtendWith(MockitoExtension::class) // JUnit 5でMockitoのアノテーション(@Mock, @InjectMocks)を有効にする
class SectorServiceTest {

    @InjectMocks // テスト対象のクラス。@Mockアノテーションが付いたモックを自動的に注入する。
    private lateinit var sectorService: SectorService

    @Mock // モック(偽のオブジェクト)を作成する。ここではDBとのやり取りをシミュレートする。
    private lateinit var sectorRepository: SectorRepository

    @Test
    fun `findAll should return all sectors`() {
        // given: 前提条件の設定
        // モックのsectorRepositoryがfindAll()メソッドを呼ばれた場合に、あらかじめ用意したsectorsリストを返すように設定
        val sectors = listOf(Sector(id = 1, name = "sector1"), Sector(id = 2, name = "sector2"))
        mockitoWhen(sectorRepository.findAll()).thenReturn(sectors) // sectorRepository.findAll()を読んだときはsectorsを返す

        // when: テスト対象のメソッドを実行
        val result = sectorService.findAll()

        // then: 結果の検証
        // 結果の件数が2件であること、そして中身が準備したリストと一致することを確認
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(sectors)
    }

    @Test
    fun `findByName should return sector when found`() {
        // given: "不動産"という名前で検索されたら、特定のsectorオブジェクトを返すように設定
        val sector = Sector(id = 1, name = "不動産")
        mockitoWhen(sectorRepository.findByName("不動産")).thenReturn(sector)

        // when: 実際に"不動産"で検索を実行
        val result = sectorService.findByName("不動産")

        // then: 返ってきたオブジェクトがnullでなく、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("不動産")
    }

    @Test
    fun `findByName should return null when not found`() {
        // given: "unknown"という名前で検索されたら、nullを返すように設定
        mockitoWhen(sectorRepository.findByName("unknown")).thenReturn(null)

        // when: 実際に"unknown"で検索を実行
        val result = sectorService.findByName("unknown")

        // then: 結果がnullであることを確認
        assertThat(result).isNull()
    }

    @Test
    fun `findById should return sector when found`() {
        // given: IDが1で検索されたら、Optionalに包まれたsectorオブジェクトを返すように設定
        val sector = Sector(id = 1, name = "不動産")
        mockitoWhen(sectorRepository.findById(1)).thenReturn(Optional.of(sector))

        // when: 実際にID=1で検索を実行
        val result = sectorService.findById(1)

        // then: 返ってきたオブジェクトがnullでなく、IDが正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1)
    }

    @Test
    fun `findById should return null when not found`() {
        // given: IDが99で検索されたら、空のOptionalを返すように設定
        mockitoWhen(sectorRepository.findById(99)).thenReturn(Optional.empty())

        // when: 実際にID=99で検索を実行
        val result = sectorService.findById(99)

        // then: 結果がnullであることを確認 (Service層でOptional.empty()がnullに変換される)
        assertThat(result).isNull()
    }

    @Test
    fun `save should return saved sector`() {
        // given: 特定のsectorオブジェクトを保存しようとしたら、IDが採番されたsectorオブジェクトを返すように設定
        val sectorToSave = Sector(name = "情報")
        val savedSector = Sector(id = 1, name = "情報")
        mockitoWhen(sectorRepository.save(sectorToSave)).thenReturn(savedSector)

        // when: 実際に保存処理を実行
        val result = sectorService.save(sectorToSave)

        // then: 返ってきたオブジェクトがnullでなく、IDが採番され、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("情報")
    }
}
