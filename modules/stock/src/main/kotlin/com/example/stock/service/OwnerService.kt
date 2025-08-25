package com.example.stock.service

import com.example.stock.model.Owner
import com.example.stock.repository.OwnerRepository
import org.springframework.stereotype.Service

@Service
open class OwnerService(private val ownerRepository: OwnerRepository) {
    open fun findAll(): List<Owner> {
        return ownerRepository.findAll()
    }

    open fun findByName(name: String): Owner? {
        return ownerRepository.findByName(name)
    }

    /**
     * IDに基づいて所有者を検索します。
     * @param id 検索する所有者のID
     * @return 見つかった所有者。存在しない場合はnull。
     */
    open fun findById(id: Int): Owner? {
        return ownerRepository.findById(id).orElse(null)
    }

    // 追加する
    open fun save(owner: Owner): Owner {
        return ownerRepository.save(owner)
    }
}
