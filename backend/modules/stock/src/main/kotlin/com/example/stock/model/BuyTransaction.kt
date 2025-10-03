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
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 取引エンティティ
 */
@Entity
@Table(name = "buy_transaction")
data class BuyTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID

    @ManyToOne
    @JoinColumn(name = "stock_id")
    val stock: Stock, // 銘柄

    @Column(name = "unit", nullable = false)
    val unit: Int, // 単元数

    @Column(name = "price", nullable = false)
    val price: BigDecimal, // 価格

    @Column(name = "fee", nullable = false)
    val fee: BigDecimal, // 手数料

    @Column(name = "transaction_date", nullable = false)
    val transaction_date: LocalDate // 取引日
)
