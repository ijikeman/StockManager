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

@Entity
@Table(name = "incoming_history")
data class IncomingHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne
    @JoinColumn(name = "lot_id")
    val stockLot: StockLot,

    @Column(name = "incoming", nullable = false)
    val incoming: BigDecimal,

    @Column(name = "payment_date", nullable = false)
    val payment_date: LocalDate
)
