package com.example.stock.dto

import com.example.stock.model.LotStatus

/**
 * 株式ロットのデータ転送オブジェクト(DTO)。
 *
 * @property id ID
 * @property owner_id 所有者のID
 * @property owner_name 所有者の名前
 * @property stock_code 銘柄コード
 * @property stock_name 銘柄名
 * @property quantity 株数
 * @property is_nisa NISA口座かどうか
 * @property status ロットのステータス
 */
data class StockLotDTO(
    val id: Int,
    val owner_id: Int,
    val owner_name: String,
    val stock_code: String,
    val stock_name: String,
    val quantity: Int,
    val is_nisa: Boolean,
    val status: LotStatus
)
