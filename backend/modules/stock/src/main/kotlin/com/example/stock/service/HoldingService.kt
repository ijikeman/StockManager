package com.example.stock.service

import com.example.stock.model.Holding
import com.example.stock.repository.HoldingRepository
import org.springframework.stereotype.Service

@Service
open class HoldingService(private val holdingRepository: HoldingRepository) {
    open fun findAll(): List<Holding> {
        return holdingRepository.findAll()
    }

    open fun findByOwnerId(owner_id: Int): Holding? {
        return holdingRepository.findByOwnerId(owner_id)
    }

    /**
     * IDに基づいて所有者を検索します。
     * @param id 検索する所有者のID
     * @return 見つかった所有者。存在しない場合はnull。
     */
    open fun findById(id: Int): Holding? {
        return holdingRepository.findById(id).orElse(null)
    }

    // 追加する
    open fun save(holding: Holding): Holding {
        return holdingRepository.save(holding)
    }

    // 削除する
    open fun deleteById(id: Int) {
        holdingRepository.deleteById(id)
    }
}
