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

/**
 * 取引履歴のエンティティ
 * 株式の売買履歴を記録します。
 *
 * @property id 取引ID (主キー)
 * @property holding この取引が関連する保有株式 ([Holding]エンティティへの参照)
 * @property transaction_type 取引種別 ("buy" または "sell")
 * @property volume 取引数量
 * @property price 取引単価
 * @property average_price_at_transaction 取引時点での平均取得単価 (売却時の損益計算に使用)
 * @property tax 取引手数料
 * @property date 取引日
 */
@Entity
@Table(name = "transaction")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    
    @ManyToOne
    @JoinColumn(name = "holding_id")
    val holding: Holding,

    @field:NotBlank(message = "取引タイプは必須です")
    @Column(name = "transaction_type", nullable = false)
    val transaction_type: String = "",

    @Column(name = "volume", nullable = false)
    val volume: Int = 0,

    @Column(name = "price", nullable = false)
    val price: Double = 0.0,

    @Column(name = "average_price_at_transaction", nullable = false)
    val average_price_at_transaction: Double = 0.0,

    @Column(name = "tax", nullable = false)
    val tax: Double = 0.0,

    @Column(name = "date", nullable = false)
    val date: java.time.LocalDate? = null
)
