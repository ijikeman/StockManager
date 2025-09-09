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
