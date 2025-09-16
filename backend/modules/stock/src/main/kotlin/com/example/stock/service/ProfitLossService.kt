package com.example.stock.service

import com.example.stock.model.LotStatus
import com.example.stock.model.StockLot
import com.example.stock.model.TransactionType
import com.example.stock.provider.FinanceProvider
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

data class ProfitLossSummary(
    // 損益サマリーを表すデータクラス
    val realizedPl: BigDecimal,
    val unrealizedPl: BigDecimal,
    val income: BigDecimal,
    val totalPl: BigDecimal
)

@Service
class ProfitLossService(
    // 損益計算サービス
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository,
    private val financeProvider: FinanceProvider
) {

    fun calculateTotalProfitLoss(ownerId: Int): ProfitLossSummary {
        // 指定したオーナーIDの全ての株式ロットを取得
        val lots = stockLotRepository.findByOwnerId(ownerId)

        var totalRealizedPl = BigDecimal.ZERO
        var totalUnrealizedPl = BigDecimal.ZERO
        var totalIncome = BigDecimal.ZERO

        // 各ロットごとに実現損益・含み損益・収入を集計

        for (lot in lots) {
            totalRealizedPl += calculateRealizedPlForLot(lot)
            totalUnrealizedPl += calculateUnrealizedPlForLot(lot)
            totalIncome += calculateIncomeForLot(lot.id)
        }

        return ProfitLossSummary(
                // 合計損益を計算して返却
            realizedPl = totalRealizedPl,
            unrealizedPl = totalUnrealizedPl,
            income = totalIncome,
            totalPl = totalRealizedPl + totalUnrealizedPl + totalIncome
        )
    }

    private fun calculateRealizedPlForLot(lot: StockLot): BigDecimal {
        // 指定ロットの実現損益を計算
        val transactions = transactionRepository.findByLotId(lot.id)
        // ロットに紐づく全トランザクションを取得
        val buyTransaction = transactions.firstOrNull { it.type == TransactionType.BUY }
            ?: return BigDecimal.ZERO // Should not happen if design is correct
        // 最初の購入トランザクションを取得（なければ0を返す）

        val buyPrice = buyTransaction.price

        val sellTransactions = transactions.filter { it.type == TransactionType.SELL }
        // 売却トランザクションのみ抽出

        return sellTransactions.sumOf { sell ->
            val quantity = sell.unit * sell.lot.stock.minimalUnit
            (sell.price - buyPrice) * quantity.toBigDecimal() - sell.fee
            // 各売却ごとに損益を計算し合計
        }
    }

    private fun calculateUnrealizedPlForLot(lot: StockLot): BigDecimal {
        // 指定ロットの含み損益を計算
        if (lot.status == LotStatus.SOLD) {
            return BigDecimal.ZERO
            // 既に売却済みの場合は0を返す
        }

        val transactions = transactionRepository.findByLotId(lot.id)
        // ロットに紐づく全トランザクションを取得
        val buyTransaction = transactions.firstOrNull { it.type == TransactionType.BUY }
            ?: return BigDecimal.ZERO
        // 最初の購入トランザクションを取得（なければ0を返す）

        val buyPrice = buyTransaction.price

        val soldQuantity = transactions.filter { it.type == TransactionType.SELL }.sumOf { it.unit * it.lot.stock.minimalUnit }
        val remainingQuantity = (lot.unit * lot.stock.minimalUnit) - soldQuantity
        // 売却済み数量を差し引いた残数量を計算

        if (remainingQuantity <= 0) {
            return BigDecimal.ZERO
                // 残数量が0以下なら含み損益なし
        }

        val stockInfo = financeProvider.fetchStockInfo(lot.stock.code)
        val currentPrice = stockInfo?.price?.toBigDecimal() ?: lot.stock.current_price.toBigDecimal()
        // 現在株価を取得（取得できなければDBの値を利用）

        if (currentPrice == BigDecimal.ZERO) {
            return BigDecimal.ZERO
                // 株価が0の場合は損益なし
        }

        return (currentPrice - buyPrice) * remainingQuantity.toBigDecimal()
        // 含み損益を計算して返却
    }

    private fun calculateIncomeForLot(lotId: Int): BigDecimal {
        // 指定ロットの配当・利益を合算
        val incoming = incomingHistoryRepository.findByLotId(lotId).sumOf { it.incoming }
        val benefits = benefitHistoryRepository.findByLotId(lotId).sumOf { it.benefit }
        return incoming + benefits
        // 合計収入を返却
    }
}
