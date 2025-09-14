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
    val realizedPl: BigDecimal,
    val unrealizedPl: BigDecimal,
    val income: BigDecimal,
    val totalPl: BigDecimal
)

@Service
class ProfitLossService(
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository,
    private val financeProvider: FinanceProvider
) {

    fun calculateTotalProfitLoss(ownerId: Int): ProfitLossSummary {
        val lots = stockLotRepository.findByOwnerId(ownerId)

        var totalRealizedPl = BigDecimal.ZERO
        var totalUnrealizedPl = BigDecimal.ZERO
        var totalIncome = BigDecimal.ZERO

        for (lot in lots) {
            totalRealizedPl += calculateRealizedPlForLot(lot)
            totalUnrealizedPl += calculateUnrealizedPlForLot(lot)
            totalIncome += calculateIncomeForLot(lot.id)
        }

        return ProfitLossSummary(
            realizedPl = totalRealizedPl,
            unrealizedPl = totalUnrealizedPl,
            income = totalIncome,
            totalPl = totalRealizedPl + totalUnrealizedPl + totalIncome
        )
    }

    private fun calculateRealizedPlForLot(lot: StockLot): BigDecimal {
        val transactions = transactionRepository.findByStockLotId(lot.id)
        val buyTransaction = transactions.firstOrNull { it.type == TransactionType.BUY }
            ?: return BigDecimal.ZERO // Should not happen if design is correct

        val buyPrice = buyTransaction.price

        val sellTransactions = transactions.filter { it.type == TransactionType.SELL }

        return sellTransactions.sumOf { sell ->
            (sell.price - buyPrice) * sell.quantity.toBigDecimal() - sell.tax
        }
    }

    private fun calculateUnrealizedPlForLot(lot: StockLot): BigDecimal {
        if (lot.status == LotStatus.SOLD) {
            return BigDecimal.ZERO
        }

        val transactions = transactionRepository.findByStockLotId(lot.id)
        val buyTransaction = transactions.firstOrNull { it.type == TransactionType.BUY }
            ?: return BigDecimal.ZERO

        val buyPrice = buyTransaction.price

        val soldQuantity = transactions.filter { it.type == TransactionType.SELL }.sumOf { it.quantity }
        val remainingQuantity = lot.quantity - soldQuantity

        if (remainingQuantity <= 0) {
            return BigDecimal.ZERO
        }

        val stockInfo = financeProvider.fetchStockInfo(lot.stock.code)
        val currentPrice = stockInfo?.price?.toBigDecimal() ?: lot.stock.current_price.toBigDecimal()

        if (currentPrice == BigDecimal.ZERO) {
            return BigDecimal.ZERO
        }

        return (currentPrice - buyPrice) * remainingQuantity.toBigDecimal()
    }

    private fun calculateIncomeForLot(lotId: Int): BigDecimal {
        val incoming = incomingHistoryRepository.findByStockLotId(lotId).sumOf { it.incoming }
        val benefits = benefitHistoryRepository.findByStockLotId(lotId).sumOf { it.benefit }
        return incoming + benefits
    }
}
