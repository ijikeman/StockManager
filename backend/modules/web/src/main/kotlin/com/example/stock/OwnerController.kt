package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

/**
 * オーナー情報に関するRESTful APIを提供します。
 */
@RestController
@RequestMapping("/api")
class OwnerController(
    private val ownerService: OwnerService
) {
    /**
     * すべてのオーナーのリストを取得します。
     * @return オーナーのリスト
     */
    @GetMapping("/owners")
    fun getOwners(): List<Owner> {
        return ownerService.findAll()
    }

    /**
     * 新しいオーナーを作成します。
     * @param owner 作成するオーナーの情報
     * @return 作成されたオーナーの情報
     */
    @PostMapping("/owners")
    fun createOwner(@Validated @RequestBody owner: Owner): ResponseEntity<Owner> {
        val savedOwner = ownerService.save(owner)
        return ResponseEntity(savedOwner, HttpStatus.CREATED)
    }

    /**
     * 既存のオーナーを更新します。
     * @param id 更新するオーナーのID
     * @param owner 更新後のオーナーの情報
     * @return 更新されたオーナーの情報
     */
    @PutMapping("/owners/{id}")
    fun updateOwner(@PathVariable id: Int, @Validated @RequestBody owner: Owner): ResponseEntity<Owner> {
        val ownerToUpdate = owner.copy(id = id)
        val updatedOwner = ownerService.save(ownerToUpdate)
        return ResponseEntity.ok(updatedOwner)
    }

    /**
     * 指定されたIDのオーナーを削除します。
     * @param id 削除するオーナーのID
     * @return レスポンスエンティティ
     */
    @DeleteMapping("/owners/{id}")
    fun deleteOwner(@PathVariable id: Int): ResponseEntity<Void> {
        ownerService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}
