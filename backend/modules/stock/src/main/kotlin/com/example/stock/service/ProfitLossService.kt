package com.example.stock.service

import com.example.stock.model.Holding
import com.example.stock.repository.HoldingRepository
import com.example.stock.repository.TransactionRepository
import com.example.stock.repository.IncomeHistoryRepository
import com.example.stock.provider.FinanceProvider
import org.springframework.stereotype.Service

data class ProfitLossSummary(
    val realizedPl: Double,
    val unrealizedPl: Double,
    val dividendIncome: Double,
    val totalPl: Double
)

@Service
class ProfitLossService(
    private val holdingRepository: HoldingRepository,
    private val transactionRepository: TransactionRepository,
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val financeProvider: FinanceProvider
) {

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

    private fun calculateRealizedPlForHolding(holdingId: Int): Double {
        val sellTransactions = transactionRepository.findByHoldingId(holdingId)
            .filter { it.transaction_type.equals("sell", ignoreCase = true) }

        val holding = holdingRepository.findById(holdingId).orElse(null) ?: return 0.0

        return sellTransactions.sumOf { transaction ->
            (transaction.price - holding.average_price) * transaction.volume - transaction.tax
        }
    }

    private fun calculateUnrealizedPlForHolding(holding: Holding): Double {
        val stockInfo = financeProvider.fetchStockInfo(holding.stock.code)
        val currentPrice = stockInfo?.currentPrice ?: holding.stock.current_price

        if (currentPrice == 0.0) {
            return 0.0 // Or handle as an error
        }

        return (currentPrice - holding.average_price) * holding.current_volume
    }

    private fun calculateDividendIncomeForHolding(holdingId: Int): Double {
        return incomeHistoryRepository.findByHoldingId(holdingId)
            .sumOf { it.amount }
    }
}
