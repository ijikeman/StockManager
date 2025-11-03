package com.example.stock

import com.example.stock.dto.BenefitHistoryAddDto
import com.example.stock.model.BenefitHistory
import com.example.stock.service.BenefitHistoryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/benefithistory")
class BenefitHistoryController(
    private val benefitHistoryService: BenefitHistoryService
) {

    @GetMapping
    fun getBenefitHistories(): List<BenefitHistory> {
        return benefitHistoryService.findAll()
    }

    /* 特定のBenefitHistoryを渡す */
    @GetMapping("/{id}")
    fun getBenefitHistoryById(@PathVariable id: Int): ResponseEntity<BenefitHistory> {
        return try {
            val benefitHistory = benefitHistoryService.findById(id)
            ResponseEntity(benefitHistory, HttpStatus.OK)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping
    fun createBenefitHistory(@Validated @RequestBody benefitHistoryDto: BenefitHistoryAddDto): ResponseEntity<BenefitHistory> {
        val created = benefitHistoryService.create(benefitHistoryDto)
        return ResponseEntity(created, HttpStatus.CREATED)
    }

    /**
     * 優待情報を更新します
     * @param id 更新対象の優待情報ID
     * @param benefitHistoryDto 更新する優待情報
     * @return 更新された優待情報
     */
    @PutMapping("/{id}")
    fun updateBenefitHistory(
        @PathVariable id: Int,
        @Validated @RequestBody benefitHistoryDto: BenefitHistoryAddDto
    ): ResponseEntity<BenefitHistory> {
        return try {
            val updated = benefitHistoryService.update(id, benefitHistoryDto)
            ResponseEntity(updated, HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    /**
     * 優待情報を削除します
     * @param id 削除対象の優待情報ID
     * @return 削除結果
     */
    @DeleteMapping("/{id}")
    fun deleteBenefitHistory(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            benefitHistoryService.delete(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}
