package com.example.stock.repository

import com.example.stock.model.Sector
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
class SectorRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager // テスト用のデータベースに対して、テストの「準備」や「結果確認」を行うために使われる

    @Autowired
    private lateinit var sectorRepository: SectorRepository

    // --- 正常系 ---
    // 正しくfindByIdできるか
    @Test
    fun `findById should return sector when sector exists`() {
        // given テスト準備としてあらかじめデータを入れる
        val sector = Sector(name = "不動産")
        // persistした結果、自動採番されたIDを含むインスタンスが返る
        val persistedSector = entityManager.persist(sector)
        entityManager.flush()

        // when 採番された番号で、findById()を検索テスト実行
        val found = sectorRepository.findById(persistedSector.id)

        // then データを取得できていることを確認
        assertThat(found).isNotNull
//        assertThat(found.get().name).isEqualTo(sector.name) // どっちでもよい
        assertEquals(found.get().name, "不動産")
    }

    // 正しくfindByNameできるか
    @Test
    fun `findByName should return sector when sector exists`() {
        // given テスト準備としてあらかじめデータを入れる
        val sector = Sector(name = "不動産")
        entityManager.persist(sector)
        entityManager.flush()

        // when テスト実行
        val found = sectorRepository.findByName("不動産")

        // then データを取得できていることを確認
        assertThat(found).isNotNull
        assertThat(found?.name).isEqualTo(sector.name)
    }

    // save()で保存できるか
    @Test
    fun `save should persist sector`() {
        // given
        val sector = Sector(name = "鉄鋼")

        // when
        val savedSector = sectorRepository.save(sector)

        // then
        val foundInDb = entityManager.find(Sector::class.java, savedSector.id) // SectorのクラスのテーブルのsaveSector.idのデータを取得
        assertThat(foundInDb).isNotNull // データが存在するか
        assertThat(foundInDb.name).isEqualTo(sector.name) // データが正しく保存できているか
    }

    // --- 異常系 ---
    // 存在しないユーザを検索(テストデータベースはクリアされているため、正しくテストできる)
    @Test
    fun `findByName should return null when sector does not exist`() {
        // when
        val found = sectorRepository.findByName("不動産")

        // then
        assertThat(found).isNull()
    }

    // 存在しないユーザをfindByIdを実行する
    @Test
    fun `findById should return sector`() {
        // given
        val sector = Sector(name = "不動産")
        val persistedSector = entityManager.persist(sector)
        entityManager.flush()

        // when
        val found = sectorRepository.findById(persistedSector.id + 99999)

        // then
        assertThat(found).isNotPresent
    }
}
