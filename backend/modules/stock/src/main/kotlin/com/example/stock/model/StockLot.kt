package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType

/**
 * 保有状況
 */
enum class LotStatus {
    HOLDING, // 保有
    SOLD     // 売却済み
}

/**
 * 株式ロット エンティティ
 */
@Entity
@Table(name = "stock_lot")
data class StockLot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: Owner, // 所有者

    @ManyToOne
    @JoinColumn(name = "stock_id")
    val stock: Stock, // 銘柄

    @Column(name = "quantity", nullable = false)
    val quantity: Int, // 最低単元数

    @Column(name = "is_nisa", nullable = false)
    val isNisa: Boolean = false, // NISAかどうか

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: LotStatus = LotStatus.HOLDING // 保有状況
)
