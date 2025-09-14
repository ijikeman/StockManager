package com.example.stock

import com.example.stock.dto.IncomingHistoryAddRequest
import com.example.stock.dto.IncomingHistoryDTO
import com.example.stock.service.IncomingHistoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/incoming-history")
class IncomingHistoryController(
    private val incomingHistoryService: IncomingHistoryService
) {

    @GetMapping
    fun getIncomingHistories(): List<IncomingHistoryDTO> {
        return incomingHistoryService.findAllIncomingHistories()
    }

    @PostMapping
    fun createIncomingHistory(@Validated @RequestBody request: IncomingHistoryAddRequest): ResponseEntity<IncomingHistoryDTO> {
        val created = incomingHistoryService.createIncomingHistory(request)
        return ResponseEntity(created, HttpStatus.CREATED)
    }
}
