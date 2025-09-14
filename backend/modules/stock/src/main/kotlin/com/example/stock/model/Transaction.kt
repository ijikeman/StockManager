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
 * 取引種別
 */
enum class TransactionType {
    BUY,  // 購入
    SELL // 売却
}

/**
 * 取引エンティティ
 */
@Entity
@Table(name = "transaction")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID

    @ManyToOne
    @JoinColumn(name = "lot_id")
    val stockLot: StockLot, // 株式ロット

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    val type: TransactionType, // 取引種別

    @Column(name = "quantity", nullable = false)
    val quantity: Int, // 数量

    @Column(name = "price", nullable = false)
    val price: BigDecimal, // 価格

    @Column(name = "tax", nullable = false)
    val tax: BigDecimal, // 税金

    @Column(name = "transaction_date", nullable = false)
    val transaction_date: LocalDate // 取引日
)
