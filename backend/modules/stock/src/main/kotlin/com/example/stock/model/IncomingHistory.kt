package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.math.BigDecimal
import java.time.LocalDate

/**
 * 入金履歴エンティティ
 */
@Entity
@Table(name = "incoming_history")
data class IncomingHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID

    @ManyToOne
    @JoinColumn(name = "lot_id")
    var stockLot: StockLot, // 株式ロット

    @Column(name = "incoming", nullable = false)
    var incoming: BigDecimal, // 入金額

    @Column(name = "payment_date", nullable = false)
    var payment_date: LocalDate // 支払日
)