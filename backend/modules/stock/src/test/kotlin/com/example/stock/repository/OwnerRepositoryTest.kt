package com.example.stock.repository

import com.example.stock.model.Owner
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.TestPropertySource

@DataJpaTest
// schema.sql,data.sqlを使っているため、テストができない。そのため初期化を無効化します
// @TestPropertySource(properties = ["spring.sql.init.mode=never"])
class OwnerRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager // テスト用のデータベースに対して、テストの「準備」や「結果確認」を行うために使われる

    @Autowired
    private lateinit var ownerRepository: OwnerRepository

    // --- 正常系 ---
    // 正しくfindByIdできるか
    @Test
    fun `findById should return owner when owner exists`() {
        // given テスト準備としてあらかじめデータを入れる
        val owner = Owner(name = "testuser")
        // persistした結果、自動採番されたIDを含むインスタンスが返る
        val persistedOwner = entityManager.persist(owner)
        entityManager.flush()

        // when 採番された番号で、findById()を検索テスト実行
        val found = ownerRepository.findById(persistedOwner.id)

        // then データを取得できていることを確認
        assertThat(found).isNotNull
//        assertThat(found.get().name).isEqualTo(owner.name) // どっちでもよい
        assertEquals(found.get().name, "testuser")
    }

    // 正しくfindByNameできるか
    @Test
    fun `findByName should return owner when owner exists`() {
        // given テスト準備としてあらかじめデータを入れる
        val owner = Owner(name = "testuser")
        entityManager.persist(owner)
        entityManager.flush()

        // when テスト実行
        val found = ownerRepository.findByName("testuser")

        // then データを取得できていることを確認
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo(owner.name)
    }

    // save()で保存できるか
    @Test
    fun `save should persist owner`() {
        // given
        val owner = Owner(name = "newuser")

        // when
        val savedOwner = ownerRepository.save(owner)

        // then
        val foundInDb = entityManager.find(Owner::class.java, savedOwner.id) // OwnerのクラスのテーブルのsaveOwner.idのデータを取得
        assertThat(foundInDb).isNotNull // データが存在するか
        assertThat(foundInDb.name).isEqualTo(owner.name) // データが正しく保存できているか
    }

    // --- 異常系 ---
    // 存在しないユーザを検索(テストデータベースはクリアされているため、正しくテストできる)
    @Test
    fun `findByName should return null when owner does not exist`() {
        // when
        val found = ownerRepository.findByName("testuser")

        // then
        assertThat(found).isNull()
    }

    // 存在しないユーザをfindByIdを実行する
    @Test
    fun `findById should return owner`() {
        // given
        val owner = Owner(name = "testuser")
        val persistedOwner = entityManager.persist(owner)
        entityManager.flush()

        // when
        val found = ownerRepository.findById(persistedOwner.id + 99999)

        // then
        assertThat(found).isNotPresent
    }
}
