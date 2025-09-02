package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.repository.OwnerRepository
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
 * OwnerServiceの単体テストクラス。
 * Mockitoフレームワークを利用して、Repository層をモック化し、Service層のロジックのみをテストする。
 */
@ExtendWith(MockitoExtension::class) // JUnit 5でMockitoのアノテーション(@Mock, @InjectMocks)を有効にする
class OwnerServiceTest {

    @InjectMocks // テスト対象のクラス。@Mockアノテーションが付いたモックを自動的に注入する。
    private lateinit var ownerService: OwnerService

    @Mock // モック(偽のオブジェクト)を作成する。ここではDBとのやり取りをシミュレートする。
    private lateinit var ownerRepository: OwnerRepository

    @Test
    fun `findAll should return all owners`() {
        // given: 前提条件の設定
        // モックのownerRepositoryがfindAll()メソッドを呼ばれた場合に、あらかじめ用意したownersリストを返すように設定
        val owners = listOf(Owner(id = 1, name = "owner1"), Owner(id = 2, name = "owner2"))
        mockitoWhen(ownerRepository.findAll()).thenReturn(owners) // owerRepository.findAll()を読んだときはownersを返す

        // when: テスト対象のメソッドを実行
        val result = ownerService.findAll()

        // then: 結果の検証
        // 結果の件数が2件であること、そして中身が準備したリストと一致することを確認
        assertThat(result).hasSize(2)
        assertThat(result).isEqualTo(owners)
    }

    @Test
    fun `findByName should return owner when found`() {
        // given: "testuser"という名前で検索されたら、特定のownerオブジェクトを返すように設定
        val owner = Owner(id = 1, name = "testuser")
        mockitoWhen(ownerRepository.findByName("testuser")).thenReturn(owner)

        // when: 実際に"testuser"で検索を実行
        val result = ownerService.findByName("testuser")

        // then: 返ってきたオブジェクトがnullでなく、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.name).isEqualTo("testuser")
    }

    @Test
    fun `findByName should return null when not found`() {
        // given: "unknown"という名前で検索されたら、nullを返すように設定
        mockitoWhen(ownerRepository.findByName("unknown")).thenReturn(null)

        // when: 実際に"unknown"で検索を実行
        val result = ownerService.findByName("unknown")

        // then: 結果がnullであることを確認
        assertThat(result).isNull()
    }

    @Test
    fun `findById should return owner when found`() {
        // given: IDが1で検索されたら、Optionalに包まれたownerオブジェクトを返すように設定
        val owner = Owner(id = 1, name = "testuser")
        mockitoWhen(ownerRepository.findById(1)).thenReturn(Optional.of(owner))

        // when: 実際にID=1で検索を実行
        val result = ownerService.findById(1)

        // then: 返ってきたオブジェクトがnullでなく、IDが正しいことを確認
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1)
    }

    @Test
    fun `findById should return null when not found`() {
        // given: IDが99で検索されたら、空のOptionalを返すように設定
        mockitoWhen(ownerRepository.findById(99)).thenReturn(Optional.empty())

        // when: 実際にID=99で検索を実行
        val result = ownerService.findById(99)

        // then: 結果がnullであることを確認 (Service層でOptional.empty()がnullに変換される)
        assertThat(result).isNull()
    }

    @Test
    fun `save should return saved owner`() {
        // given: 特定のownerオブジェクトを保存しようとしたら、IDが採番されたownerオブジェクトを返すように設定
        val ownerToSave = Owner(name = "newowner")
        val savedOwner = Owner(id = 1, name = "newowner")
        mockitoWhen(ownerRepository.save(ownerToSave)).thenReturn(savedOwner)

        // when: 実際に保存処理を実行
        val result = ownerService.save(ownerToSave)

        // then: 返ってきたオブジェクトがnullでなく、IDが採番され、名前が正しいことを確認
        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(1)
        assertThat(result.name).isEqualTo("newowner")
    }
}
