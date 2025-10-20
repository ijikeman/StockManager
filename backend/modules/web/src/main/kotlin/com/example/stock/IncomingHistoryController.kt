package com.example.stock

import com.example.stock.dto.IncomingHistoryAddDto
import com.example.stock.model.IncomingHistory
import com.example.stock.service.IncomingHistoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/incominghistory")
class IncomingHistoryController(
    private val incomingHistoryService: IncomingHistoryService
) {

    @GetMapping
    fun getIncomingHistories(): List<IncomingHistory> {
        return incomingHistoryService.findAll()
    }

    /* 特定のIncomingHistoryを渡す */
    @GetMapping("/{id}")
    fun getIncomingHistoryById(@PathVariable id: Int): ResponseEntity<IncomingHistory> {
        val incomingHistory = incomingHistoryService.findById(id)
        return if (incomingHistory != null) {
            ResponseEntity(incomingHistory, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping
    fun createIncomingHistory(@Validated @RequestBody incomingHistoryDto: IncomingHistoryAddDto): ResponseEntity<IncomingHistory> {
        val created = incomingHistoryService.create(incomingHistoryDto)
        return ResponseEntity(created, HttpStatus.CREATED)
    }

    /**
     * 配当情報を更新します
     * @param id 更新対象の配当情報ID
     * @param incomingHistoryDto 更新する配当情報
     * @return 更新された配当情報
     */
    @PutMapping("/{id}")
    fun updateIncomingHistory(
        @PathVariable id: Int,
        @Validated @RequestBody incomingHistoryDto: IncomingHistoryAddDto
    ): ResponseEntity<IncomingHistory> {
        return try {
            val updated = incomingHistoryService.update(id, incomingHistoryDto)
            ResponseEntity(updated, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}
