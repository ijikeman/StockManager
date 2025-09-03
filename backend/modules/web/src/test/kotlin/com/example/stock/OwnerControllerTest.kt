package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.ArgumentMatchers.any

@WebMvcTest(OwnerController::class)
class OwnerControllerTest {

    @TestConfiguration
    class ControllerTestConfig {
        @Bean
        fun objectMapper(): ObjectMapper {
            return ObjectMapper()
        }
    }

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var ownerService: OwnerService

    @Test
    fun `getOwners should return list of owners`() {
        val owners = listOf(Owner(id = 1, name = "Test Owner"))
        `when`(ownerService.findAll()).thenReturn(owners)

        mockMvc.perform(get("/api/owners"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].name").value("Test Owner"))
    }

    @Test
    fun `createOwner should create owner and return it`() {
        val owner = Owner(name = "New Owner")
        val savedOwner = Owner(id = 1, name = "New Owner")
        `when`(ownerService.save(any(Owner::class.java))).thenReturn(savedOwner)

        mockMvc.perform(post("/api/owners")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(owner)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("New Owner"))
    }

    @Test
    fun `updateOwner should update owner and return it`() {
        val owner = Owner(name = "Updated Owner")
        val updatedOwner = Owner(id = 1, name = "Updated Owner")
        `when`(ownerService.save(any(Owner::class.java))).thenReturn(updatedOwner)

        mockMvc.perform(put("/api/owners/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(owner)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Updated Owner"))
    }

    @Test
    fun `deleteOwner should delete owner and return no content`() {
        mockMvc.perform(delete("/api/owners/1"))
            .andExpect(status().isNoContent)
    }
}
