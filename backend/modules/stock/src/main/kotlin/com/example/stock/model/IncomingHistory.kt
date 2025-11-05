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

/**
 * 入金履歴エンティティ
 * 株式ロットが売却された場合にこのincoming_historyレコードが指定された場合は、
 * stock_lot_idはnullになり、代わりにsell_transaction_idは指定されます。
 */
@Entity
@Table(name = "incoming_history")
data class IncomingHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID


    @ManyToOne
    @JoinColumn(name = "stock_lot_id")
    var stockLot: StockLot? = null, // 株式ロット（売却時はnull）

    @ManyToOne
    @JoinColumn(name = "sell_transaction_id")
    var sellTransaction: SellTransaction? = null, // 売却取引（通常はnull）

    @Column(name = "incoming", nullable = false)
    var incoming: BigDecimal, // 入金額

    @Column(name = "payment_date", nullable = false)
    var paymentDate: LocalDate, // 支払日

    @Column(name = "is_nisa", nullable = false)
    var isNisa: Boolean = false // NISAかどうか
)
{
    init {
        // stockLotとsellTransactionのどちらか一方のみがセットされていること
        if ((stockLot == null && sellTransaction == null) || (stockLot != null && sellTransaction != null)) {
            throw IllegalArgumentException("stockLotとsellTransactionのどちらか一方のみを指定してください")
        }
    }
}
