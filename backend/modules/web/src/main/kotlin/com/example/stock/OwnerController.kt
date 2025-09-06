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
    @GetMapping("/owner")
    fun getOwners(): List<Owner> {
        // オーナーサービスを通じて全オーナー情報を取得し、返す
        return ownerService.findAll()
    }

    /**
     * IDを指定してオーナーを取得します。
     * @param id 取得するオーナーのID
     * @return 見つかったオーナー。見つからない場合は404 Not Found。
     */
    @GetMapping("/owner/{id}")
    fun getOwner(@PathVariable id: Int): ResponseEntity<Owner> {
        val owner = ownerService.findById(id)
        return if (owner != null) {
            ResponseEntity.ok(owner)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * 新しいオーナーを作成します。
     * @param owner 作成するオーナーの情報
     * @return 作成されたオーナーの情報
     */
    @PostMapping("/owner")
    fun createOwner(@Validated @RequestBody owner: Owner): ResponseEntity<Owner> {
        // リクエストボディで受け取ったオーナー情報を保存
        val savedOwner = ownerService.save(owner)
        // 保存されたオーナー情報とHTTPステータス201 (CREATED) を返す
        return ResponseEntity(savedOwner, HttpStatus.CREATED)
    }

    /**
     * 既存のオーナーを更新します。
     * @param id 更新するオーナーのID
     * @param owner 更新後のオーナーの情報
     * @return 更新されたオーナーの情報
     */
    @PutMapping("/owner/{id}")
    fun updateOwner(@PathVariable id: Int, @Validated @RequestBody owner: Owner): ResponseEntity<Owner> {
        // パス変数で受け取ったIDをオーナー情報に設定
        val ownerToUpdate = owner.copy(id = id)
        // 更新されたオーナー情報を保存
        val updatedOwner = ownerService.save(ownerToUpdate)
        // 更新されたオーナー情報とHTTPステータス200 (OK) を返す
        return ResponseEntity.ok(updatedOwner)
    }

    /**
     * 指定されたIDのオーナーを削除します。
     * @param id 削除するオーナーのID
     * @return レスポンスエンティティ
     */
    @DeleteMapping("/owner/{id}")
    fun deleteOwner(@PathVariable id: Int): ResponseEntity<Void> {
        // 指定されたIDのオーナー情報を削除
        ownerService.deleteById(id)
        // HTTPステータ-タス204 (No Content) を返す
        return ResponseEntity.noContent().build()
    }
}
