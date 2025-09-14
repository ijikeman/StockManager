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

enum class LotStatus {
    HOLDING,
    SOLD
}

@Entity
@Table(name = "stock_lot")
data class StockLot(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "owner_id")
    val owner: Owner,

    @ManyToOne
    @JoinColumn(name = "stock_id")
    val stock: Stock,

    @Column(name = "quantity", nullable = false)
    val quantity: Int,

    @Column(name = "is_nisa", nullable = false)
    val isNisa: Boolean = false,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: LotStatus = LotStatus.HOLDING
)
