package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.repository.OwnerRepository
import org.springframework.stereotype.Service

@Service
open class OwnerService(
    private val ownerRepository: OwnerRepository
) {
    /**
     * すべての所有者を取得します。
     */
    fun findAll(): List<Owner> {
        return ownerRepository.findAll()
    }

    /**
     * IDに基づいて所有者を検索します。
     * @param id 検索する所有者のID
     * @return 見つかった所有者。存在しない場合はnull。
     */
    fun findById(id: Int): Owner? {
        return ownerRepository.findById(id).orElse(null)
    }

    /**
     * 所有者を保存します。
     * @param owner 保存する所有者
     * @return 保存された所有者
     */
    fun save(owner: Owner): Owner {
        return ownerRepository.save(owner)
    }

    /**
     * 所有者を削除します。
     * @param id 削除する所有者のID
     */
    open fun deleteById(id: Int) {
        ownerRepository.deleteById(id)
    }
}
