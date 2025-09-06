package com.example.stock

import com.example.stock.model.Sector
import com.example.stock.service.SectorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * セクター情報に関するRESTful APIを提供します。
 */
@RestController
@RequestMapping("/api")
class SectorController(
    private val sectorService: SectorService
) {
    /**
     * すべてのセクターのリストを取得します。
     * @return セクターのリスト
     */
    @GetMapping("/sector")
    fun getSectors(): List<Sector> {
        return sectorService.findAll()
    }

    /**
     * IDを指定してセクターを取得します。
     * @param id 取得するセクターのID
     * @return 見つかったセクター。見つからない場合は404 Not Found。
     */
    @GetMapping("/sector/{id}")
    fun getSector(@PathVariable id: Int): ResponseEntity<Sector> {
        val sector = sectorService.findById(id)
        return if (sector != null) {
            ResponseEntity.ok(sector)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * 新しいセクターを作成します。
     * @param sector 作成するセクターの情報
     * @return 作成されたセクターの情報
     */
    @PostMapping("/sector")
    fun createSector(@Validated @RequestBody sector: Sector): ResponseEntity<Sector> {
        val savedSector = sectorService.save(sector)
        return ResponseEntity(savedSector, HttpStatus.CREATED)
    }

    /**
     * 既存のセクターを更新します。
     * @param id 更新するセクターのID
     * @param sector 更新後のセクターの情報
     * @return 更新されたセクターの情報
     */
    @PutMapping("/sector/{id}")
    fun updateSector(@PathVariable id: Int, @Validated @RequestBody sector: Sector): ResponseEntity<Sector> {
        val sectorToUpdate = sector.copy(id = id)
        val updatedSector = sectorService.save(sectorToUpdate)
        return ResponseEntity.ok(updatedSector)
    }

    /**
     * 指定されたIDのセクターを削除します。
     * @param id 削除するセクターのID
     * @return レスポンスエンティティ
     */
    @DeleteMapping("/sector/{id}")
    fun deleteSector(@PathVariable id: Int): ResponseEntity<Void> {
        sectorService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
