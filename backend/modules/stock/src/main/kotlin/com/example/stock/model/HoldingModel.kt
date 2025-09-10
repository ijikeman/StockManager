package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

/**
 * 保有株式のエンティティ
 * どのユーザーがどの銘柄をどれだけ保有しているかを管理します。
 *
 * @property id 保有ID (主キー)
 * @property owner 株式を保有するユーザー ([Owner]エンティティへの参照)
 * @property stock 保有する株式銘柄 ([Stock]エンティティへの参照)
 * @property nisa NISA口座での保有かどうかを示すフラグ
 * @property current_volume 現在の保有数量
 * @property average_price 平均取得単価
 * @property updated_at 最終更新日時
 */
@Entity
@Table(name = "holding")
data class Holding (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: Owner,

    @ManyToOne
    @JoinColumn(name = "stock_id")
    val stock: Stock,

    @Column(name = "nisa", nullable = false)
    val nisa: Boolean = false,

    @Column(name = "current_volume", nullable = false)
    val current_volume: Int = 0,

    @Column(name = "average_price", nullable = false)
    val average_price: Double = 0.0,

    @Column(name = "updated_at")
    val updated_at: java.time.LocalDate? = null
)
