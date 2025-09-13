package com.example.stock

import com.example.stock.service.IncomeHistoryService
import com.example.stock.service.IncomeHistoryRequest
import com.example.stock.model.IncomeHistory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/income")
class IncomeController(private val incomeHistoryService: IncomeHistoryService) {

    @GetMapping
    fun getAllIncomes(): ResponseEntity<List<IncomeHistory>> {
        val incomes = incomeHistoryService.findAllIncomeHistories()
        return ResponseEntity.ok(incomes)
    }

    @GetMapping("/{id}")
    fun getIncomeById(@PathVariable id: Int): ResponseEntity<IncomeHistory> {
        val income = incomeHistoryService.findIncomeHistoryById(id)
        return ResponseEntity.ok(income)
    }

    @PostMapping
    fun createIncome(@RequestBody request: IncomeHistoryRequest): ResponseEntity<IncomeHistory> {
        val createdIncome = incomeHistoryService.createIncomeHistory(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome)
    }

    @PutMapping("/{id}")
    fun updateIncome(@PathVariable id: Int, @RequestBody request: IncomeHistoryRequest): ResponseEntity<IncomeHistory> {
        val updatedIncome = incomeHistoryService.updateIncomeHistory(id, request)
        return ResponseEntity.ok(updatedIncome)
    }

    @DeleteMapping("/{id}")
    fun deleteIncome(@PathVariable id: Int): ResponseEntity<Void> {
        incomeHistoryService.deleteIncomeHistory(id)
        return ResponseEntity.noContent().build()
    }
}
