package com.example.stock.service

import com.example.stock.dto.HoldingDTO
import com.example.stock.model.LotStatus
import com.example.stock.model.TransactionType
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

@Service
@Transactional(readOnly = true)
class HoldingService(
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository
) {

    fun getHoldings(): List<HoldingDTO> {
        val holdingLots = stockLotRepository.findByStatus(LotStatus.HOLDING)

        return holdingLots
            .groupBy { it.stock }
            .map { (stock, lots) ->
                val totalQuantity = lots.sumOf { it.quantity }

                val totalCost = lots.sumOf { lot ->
                    val buyTransaction = transactionRepository.findByStockLotId(lot.id)
                        .firstOrNull { it.type == TransactionType.BUY }

                    if (buyTransaction != null) {
                        buyTransaction.price.multiply(BigDecimal(lot.quantity))
                    } else {
                        BigDecimal.ZERO
                    }
                }

                val averagePrice = if (totalQuantity > 0) {
                    totalCost.divide(BigDecimal(totalQuantity), 2, RoundingMode.HALF_UP)
                } else {
                    BigDecimal.ZERO
                }

                HoldingDTO(
                    stockCode = stock.code,
                    stockName = stock.name,
                    quantity = totalQuantity,
                    averagePrice = averagePrice,
                    bookValue = totalCost
                )
            }
    }
}
