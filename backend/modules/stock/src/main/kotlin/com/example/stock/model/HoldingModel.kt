package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.NotBlank
import jakarta.persistence.ManyToOne
import jakarta.persistence.JoinColumn

/* 
    holding {
        UUID id PK
        UUID owner_id FK "株式を保有するユーザーID"
        UUID stock_id FK "保有銘柄ID"
        integer current_volume "現在保有している株数"
        decimal average_price "購入にかかった平均取得単価"
        timestamp updated_at "最終更新日時"
    }
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
