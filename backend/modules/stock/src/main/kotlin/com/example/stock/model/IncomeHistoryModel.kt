package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn
import java.time.LocalDate

/**
 * 収益履歴のエンティティ
 * 配当金や株主優待など、売買以外の収益を記録します。
 *
 * @property id 収益履歴ID (主キー)
 * @property holding この収益が関連する保有株式 ([Holding]エンティティへの参照)
 * @property income_type 収益種別 ("dividend" や "preferential" など)
 * @property amount 金額
 * @property date 発生日
 */
@Entity
@Table(name = "income_history")
data class IncomeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "holding_id")
    val holding: Holding,

    @field:NotBlank(message = "収益タイプは必須です")
    @Column(name = "income_type", nullable = false)
    val income_type: String = "",

    @Column(name = "amount", nullable = false)
    val amount: Double = 0.0,

    @Column(name = "date", nullable = false)
    val date: LocalDate? = null
)
