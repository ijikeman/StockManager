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

/**
 * 利益履歴エンティティ
 * 株式ロットが売却された場合にこのbenefit_historyレコードが指定された場合は、
 * stock_lot_idはnullになり、代わりにsell_transaction_idは指定されます。
 */
@Entity
@Table(name = "benefit_history")
data class BenefitHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0, // ID


    @ManyToOne
    @JoinColumn(name = "stock_lot_id")
    var stockLot: StockLot? = null, // 株式ロット（売却時はnull）

    @ManyToOne
    @JoinColumn(name = "sell_transaction_id")
    var sellTransaction: SellTransaction? = null, // 売却取引（通常はnull）

    @Column(name = "benefit", nullable = false)
    val benefit: BigDecimal, // 利益

    @Column(name = "payment_date", nullable = false)
    var paymentDate: java.time.LocalDate // 支払日
)
{
    init {
        // stockLotとsellTransactionのどちらか一方のみがセットされていること
        if ((stockLot == null && sellTransaction == null) || (stockLot != null && sellTransaction != null)) {
            throw IllegalArgumentException("stockLotとsellTransactionのどちらか一方のみを指定してください")
        }
    }
}
