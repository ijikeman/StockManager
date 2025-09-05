package com.example.stock

import com.example.stock.model.Sector
import com.example.stock.service.SectorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class SectorController(
    private val sectorService: SectorService
) {
    @GetMapping("/sectors")
    fun getSectors(): ResponseEntity<List<Sector>> {
        val sectors = sectorService.findAll()
        return ResponseEntity.ok(sectors)
    }
}
