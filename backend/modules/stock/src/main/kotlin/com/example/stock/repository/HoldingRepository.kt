package com.example.stock.repository

import com.example.stock.model.Holding
import com.example.stock.model.Stock
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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

    /**
     * 指定された株式に紐づく保有株式を取得します。
     *
     * @param stock 検索対象の株式
     * @return 見つかった保有株式
     */
    fun findByStock(stock: Stock): Holding?

    /**
     * 指定された銘柄コードに紐づく保有株式を取得します。
     *
     * @param stockCode 検索対象の銘柄コード
     * @return 見つかった保有株式
     */
    @Query("SELECT h FROM Holding h JOIN h.stock s WHERE s.code = :stockCode")
    fun findByStockCode(@Param("stockCode") stockCode: String): Holding?
}
