package com.example.stock.service

import com.example.stock.model.Holding
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.TransactionRepository
import com.example.stock.repository.IncomeHistoryRepository
import com.example.stock.provider.FinanceProvider
import org.springframework.stereotype.Service

/**
 * 損益計算結果を保持するデータクラス
 *
 * @property realizedPl 確定損益
 * @property unrealizedPl 含み損益
 * @property dividendIncome 配当金・優待金収益
 * @property totalPl 合計損益
 */
data class ProfitLossSummary(
    val realizedPl: Double,
    val unrealizedPl: Double,
    val dividendIncome: Double,
    val totalPl: Double
)

/**
 * 損益計算に関するビジネスロジックを提供するサービスクラス
 *
 * @param holdingRepository 保有株式リポジトリ
 * @param transactionRepository 取引履歴リポジトリ
 * @param incomeHistoryRepository 収益履歴リポジトリ
 * @param financeProvider 株価情報提供プロバイダ
 */
@Service
class ProfitLossService(
    private val holdingRepository: HoldingRepository,
    private val transactionRepository: TransactionRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val financeProvider: FinanceProvider
) {

    /**
     * 指定されたオーナーの全保有株式にわたる損益を計算します。
     *
     * @param ownerId 計算対象のオーナーID
     * @return 損益のサマリー ([ProfitLossSummary])
     */
    fun calculateTotalProfitLoss(ownerId: Int): ProfitLossSummary {
        val holdings = holdingRepository.findByOwnerId(ownerId)

        var totalRealizedPl = 0.0
        var totalUnrealizedPl = 0.0
        var totalDividendIncome = 0.0

        for (holding in holdings) {
            totalRealizedPl += calculateRealizedPlForHolding(holding.id)
            totalUnrealizedPl += calculateUnrealizedPlForHolding(holding)
            totalDividendIncome += calculateDividendIncomeForHolding(holding.id)
        }

        return ProfitLossSummary(
            realizedPl = totalRealizedPl,
            unrealizedPl = totalUnrealizedPl,
            dividendIncome = totalDividendIncome,
            totalPl = totalRealizedPl + totalUnrealizedPl + totalDividendIncome
        )
    }

    /**
     * 特定の保有株式に関する確定損益を計算します。
     * 売却トランザクションに基づいて計算されます。
     *
     * @param holdingId 計算対象の保有ID
     * @return 確定損益額
     */
    private fun calculateRealizedPlForHolding(holdingId: Int): Double {
        val sellTransactions = transactionRepository.findByHoldingId(holdingId)
            .filter { it.transaction_type.equals("sell", ignoreCase = true) }

        return sellTransactions.sumOf { transaction ->
            (transaction.price - transaction.average_price_at_transaction) * transaction.volume - transaction.tax
        }
    }

    /**
     * 特定の保有株式に関する含み損益を計算します。
     * 現在の株価と平均取得単価の差から計算されます。
     *
     * @param holding 計算対象の保有株式
     * @return 含み損益額
     */
    private fun calculateUnrealizedPlForHolding(holding: Holding): Double {
        val stockInfo = financeProvider.fetchStockInfo(holding.stock.code)
        val currentPrice = stockInfo?.price ?: holding.stock.current_price

        if (currentPrice == 0.0) {
            return 0.0 // 現在価格が取得できない場合は0として扱う
        }

        return (currentPrice - holding.average_price) * holding.current_volume
    }

    /**
     * 特定の保有株式に関する配当金・優待金収益を計算します。
     *
     * @param holdingId 計算対象の保有ID
     * @return 配当金・優待金収益の合計額
     */
    private fun calculateDividendIncomeForHolding(holdingId: Int): Double {
        return incomeHistoryRepository.findByHoldingId(holdingId)
            .sumOf { it.amount }
    }
}
