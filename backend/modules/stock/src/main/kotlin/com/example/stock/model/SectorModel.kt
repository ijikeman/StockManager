package com.example.stock.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.GenerationType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.NotBlank


@Entity
@Table(name = "sector")
data class Sector(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    // セクター名
    @field:NotBlank(message = "セクター名は必須です")
    @Column(name = "name", nullable = false)
    val name: String = ""
)
