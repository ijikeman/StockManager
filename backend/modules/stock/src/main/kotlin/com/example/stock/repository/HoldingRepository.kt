package com.example.stock.repository

import com.example.stock.model.Holding
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * 保有株式([Holding])のリポジトリインターフェース
 * Spring Data JPA を利用して、holdingテーブルへのアクセスを提供します。
 */
@Repository
interface HoldingRepository : JpaRepository<Holding, Int> {
    /**
     * 指定されたオーナーIDに紐づくすべての保有株式を取得します。
     *
     * @param ownerId 検索対象のオーナーID
     * @return 見つかった保有株式のリスト
     */
    fun findByOwnerId(ownerId: Int): List<Holding>
}
