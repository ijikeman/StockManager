package com.example.stock.service

import com.example.stock.model.Holding
import com.example.stock.repository.HoldingRepository
import org.springframework.stereotype.Service

/**
 * 保有株式([Holding])に関するビジネスロジックを提供するサービスクラス
 * @param holdingRepository 保有株式リポジトリ
 */
@Service
open class HoldingService(private val holdingRepository: HoldingRepository) {
    /**
     * すべての保有株式を取得します。
     * @return 保有株式のリスト
     */
    open fun findAll(): List<Holding> {
        return holdingRepository.findAll()
    }

    /**
     * 指定されたオーナーIDに紐づくすべての保有株式を取得します。
     * @param ownerId 検索対象のオーナーID
     * @return 見つかった保有株式のリスト
     */
    open fun findByOwnerId(ownerId: Int): List<Holding> {
        return holdingRepository.findByOwnerId(ownerId)
    }

    /**
     * IDに基づいて保有株式を検索します。
     * @param id 検索する保有株式のID
     * @return 見つかった保有株式。存在しない場合はnull。
     */
    open fun findById(id: Int): Holding? {
        return holdingRepository.findById(id).orElse(null)
    }

    /**
     * 保有株式情報を保存（新規作成または更新）します。
     * @param holding 保存する保有株式オブジェクト
     * @return 保存された保有株式オブジェクト
     */
    open fun save(holding: Holding): Holding {
        return holdingRepository.save(holding)
    }

    /**
     * IDに基づいて保有株式を削除します。
     * @param id 削除する保有株式のID
     */
    open fun deleteById(id: Int) {
        holdingRepository.deleteById(id)
    }
}
