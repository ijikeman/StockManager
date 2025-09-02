package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.`when` as mockitoWhen
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.hasProperty
import org.hamcrest.Matchers.`is`

/**
 * OwnerControllerの単体テストクラス。
 * @WebMvcTest アノテーションを使用し、Webレイヤー（Controller）のみをテスト対象とする。
 * Service層は@MockBeanでモック化する。
 */
@WebMvcTest(OwnerController::class)
class OwnerControllerTest {

    @Autowired
    // lateinit: 遅延初期化の宣言(クラスをインスタンス化する時点では、まだSpringコンテナによる注入が行われていない可能性がある為、nullチェックが必要。それを回避)
    // クラスの初期化時にはnullだが、使用する前にはSpringによって自動的に初期化される
    private lateinit var mockMvc: MockMvc // HTTPリクエストをシミュレートするためのMockMvcオブジェクト

    @MockBean
    private lateinit var ownerService: OwnerService // Service層のモック

    /**
     * 所有者一覧ページの表示テスト。
     * GET /owner にアクセスした際に、ステータスコード200、ビュー名"owner"、
     * そして"owner"という名前のモデル属性に所有者リストが含まれていることを検証する。
     */
    @Test
    fun `ownerList should return owner view with owners`() {
        // given: 前提条件の設定
        val owners = listOf(Owner(id = 1, name = "Test Owner"))
        // ownerService.findAllを呼び出した場合はowners(モックを返す)
        mockitoWhen(ownerService.findAll()).thenReturn(owners)

        // when & then: テスト実行と結果検証
        mockMvc.perform(get("/owner")) // getメソッドで/ownerへアクセス
            .andExpect(status().isOk) // Status: 200
            .andExpect(view().name("owner")) // ownerモデルにnameが含まれている
            .andExpect(model().attributeExists("owner")) // ownerモデルが存在する
            .andExpect(model().attribute("owner", owners))
            .andExpect(model().attribute("owner", hasItem(hasProperty<Owner>("name", `is`("Test Owner"))))) // owner.nameは"Test Owner"である
    }

    /**
     * 所有者登録処理の正常系テスト。
     * POST /owner に有効なパラメータを送信した際に、ステータスリダイレクト、/owner にリダイレクトされることを検証する。
     */
    @Test
    fun `ownerRegister should redirect to owner when valid`() {
        mockMvc.perform(post("/owner").param("name", "NewOwner"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/owner"))
    }

    /**
     * 所有者登録処理の異常系（バリデーションエラー）テスト。
     * POST /owner に無効なパラメータ（空の名前）を送信した際に、ステータスコード200、
     * ビュー名"owner"が返され、モデルにエラーが含まれていることを検証する。
     */
    @Test
    fun `ownerRegister should return owner view when invalid`() {
        mockMvc.perform(post("/owner").param("name", ""))
            .andExpect(status().isOk)
            .andExpect(view().name("owner"))
            .andExpect(model().hasErrors())
    }

    /**
     * 所有者編集ページの表示テスト。
     * GET /owner/edit/{id} にアクセスした際に、ステータスコード200、ビュー名"owner_edit"、
     * そして"owner"という名前のモデル属性に該当の所有者情報が含まれていることを検証する。
     */
    @Test
    fun `ownerEdit should return owner_edit view with owner`() {
        val owner = Owner(id = 1, name = "Test Owner")
        mockitoWhen(ownerService.findById(1)).thenReturn(owner)

        mockMvc.perform(get("/owner/edit/1"))
            .andExpect(status().isOk)
            .andExpect(view().name("owner_edit"))
            .andExpect(model().attributeExists("owner"))
            .andExpect(model().attribute("owner", owner))
    }

    /**
     * 所有者更新処理の正常系テスト。
     * POST /owner/update に有効なパラメータを送信した際に、ステータスリダイレクト、/owner にリダイレクトされることを検証する。
     */
    @Test
    fun `ownerUpdate should redirect to owner when valid`() {
        mockMvc.perform(post("/owner/update").param("id", "1").param("name", "UpdatedOwner"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/owner"))
    }

    /**
     * 所有者更新処理の異常系（バリデーションエラー）テスト。
     * POST /owner/update に無効なパラメータ（空の名前）を送信した際に、ステータスコード200、
     * ビュー名"owner_edit"が返され、モデルにエラーが含まれていることを検証する。
     */
    @Test
    fun `ownerUpdate should return owner_edit view when invalid`() {
        mockMvc.perform(post("/owner/update").param("id", "1").param("name", ""))
            .andExpect(status().isOk)
            .andExpect(view().name("owner_edit"))
            .andExpect(model().hasErrors())
    }

    /**
     * 所有者削除処理のテスト。
     * POST /owner/delete/{id} にアクセスした際に、ステータスリダイレクト、/owner にリダイレクトされることを検証する。
     */
    @Test
    fun `ownerDelete should redirect to owner`() {
        mockMvc.perform(post("/owner/delete/1"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/owner"))
    }
}
