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

/**
 * 利益履歴エンティティ
 */
@Entity
@Table(name = "benefit_history")
data class BenefitHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID

    @ManyToOne
    @JoinColumn(name = "lot_id")
    val stockLot: StockLot, // 株式ロット

    @Column(name = "benefit", nullable = false)
    val benefit: BigDecimal // 利益
)