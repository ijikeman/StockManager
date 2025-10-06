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

    @Column(name = "current_unit", nullable = false)
    val current_unit: Int, // 現在の単元数
)
