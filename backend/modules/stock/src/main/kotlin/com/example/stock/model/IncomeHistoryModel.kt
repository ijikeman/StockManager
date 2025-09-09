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
