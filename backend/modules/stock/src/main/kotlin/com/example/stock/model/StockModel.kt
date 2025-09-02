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
@Table(name = "stock")
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    // 銘柄コード
    @field:NotBlank(message = "銘柄コードは必須です")
    @field:Pattern(regexp = "^[0-9a-zA-Z]+$", message = "名前は数字あるいはアルファベットのみで構成される必要があります")
    @Column(name = "code", nullable = false)
    val code: String = "",

    // 銘柄名
    @field:NotBlank(message = "銘柄名は必須です")
    @Column(name = "name", nullable = false)
    val name: String = "",

    // 現在の株価
    @Column(name = "current_price", nullable = false)
    val current_price: Double = 0.0,

    // 配当金
    @Column(name = "dividend", nullable = false)
    val dividend: Double = 0.0,

    // 業績発表日(YYYYMMDD)
    @field:Pattern(regexp = "^[0-9]{8}$", message = "日付はYYYYMMDDの形式で入力してください")
    @Column(name = "release_date", nullable = false)
    val release_date: String = ""
)
