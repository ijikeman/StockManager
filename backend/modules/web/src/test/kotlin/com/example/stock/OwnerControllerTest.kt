package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

/**
 * OwnerControllerの単体テストクラス
 */
@WebMvcTest(OwnerController::class)
class OwnerControllerTest {

    /**
     * テスト固有の設定クラス
     */
    @TestConfiguration
    class ControllerTestConfig {
        /**
         * ObjectMapperをBeanとして登録
         * @return ObjectMapper
         */
        @Bean
        fun objectMapper(): ObjectMapper {
            return ObjectMapper()
        }
    }

    // MockMvcは、コントローラーのテストを容易にするためにSpring MVCインフラを模倣します
    @Autowired
    private lateinit var mockMvc: MockMvc

    // ObjectMapperは、JavaオブジェクトとJSONを相互に変換するために使用されます
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    // OwnerServiceのモック。実際のサービスを呼び出さずにテストできるようにします
    @MockBean
    private lateinit var ownerService: OwnerService

    /**
     * GET /api/ownersのエンドポイントをテストします。
     * すべてのオーナーのリストを返すことを期待します。
     */
    @Test
    fun `getOwners should return list of owners`() {
        val owners = listOf(Owner(id = 1, name = "TestOwner"))
        whenever(ownerService.findAll()).thenReturn(owners)

        mockMvc.perform(get("/api/owners"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("TestOwner"))
    }

    /**
     * POST /api/ownersのエンドポイントをテストします。
     * 新しいオーナーを作成し、作成されたオーナーを返すことを期待します。
     */
    @Test
    fun `createOwner should create owner and return it`() {
        val owner = Owner(name = "NewOwner")
        val savedOwner = Owner(id = 1, name = "NewOwner")
        whenever(ownerService.save(any())).thenReturn(savedOwner)

        mockMvc.perform(post("/api/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(owner)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("NewOwner"))
    }

    /**
     * PUT /api/owners/{id}のエンドポイントをテストします。
     * 既存のオーナーを更新し、更新されたオーナーを返すことを期待します。
     */
    @Test
    fun `updateOwner should update owner and return it`() {
        val owner = Owner(name = "UpdatedOwner")
        val updatedOwner = Owner(id = 1, name = "UpdatedOwner")
        whenever(ownerService.save(any())).thenReturn(updatedOwner)

        mockMvc.perform(put("/api/owners/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(owner)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("UpdatedOwner"))
    }

    /**
     * DELETE /api/owners/{id}のエンドポイントをテストします。
     * オーナーを削除し、No Contentステータスを返すことを期待します。
     */
    @Test
    fun `deleteOwner should delete owner and return no content`() {
        mockMvc.perform(delete("/api/owners/1"))
            .andExpect(status().isNoContent)
    }
}
