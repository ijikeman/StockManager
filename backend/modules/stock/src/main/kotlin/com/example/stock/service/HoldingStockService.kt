package com.example.stock.service

import com.example.stock.dto.HoldingInfoDTO
import com.example.stock.model.LotStatus
import com.example.stock.model.TransactionType
import com.example.stock.provider.YahooFinanceProvider
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.TransactionRepository
import com.example.stock.model.Transaction
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class HoldingStockService(
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val yahooFinanceProvider: YahooFinanceProvider
) {

    fun getHoldingInfo(ownerId: Int?): Map<Boolean, List<HoldingInfoDTO>> {
        val stockLots = if (ownerId != null) {
            stockLotRepository.findByOwnerIdAndStatus(ownerId, LotStatus.HOLDING)
        } else {
            stockLotRepository.findByStatus(LotStatus.HOLDING)
        }

        return stockLots.map { stockLot ->
            val transaction = transactionRepository.findByStockLotAndType(stockLot, TransactionType.BUY).firstOrNull()
            val acquisitionPrice = transaction?.price ?: BigDecimal.ZERO

            val incomingHistories = incomingHistoryRepository.findByStockLot(stockLot)
            val dividend = incomingHistories.sumOf { it.incoming }

            val stockInfo = yahooFinanceProvider.fetchStockInfo(stockLot.stock.code)
            val currentPrice = stockInfo?.price?.toBigDecimal() ?: BigDecimal.ZERO

            val quantity = stockLot.unit * stockLot.stock.minimalUnit
            val profitLoss = (currentPrice - acquisitionPrice).multiply(BigDecimal(quantity))

            HoldingInfoDTO(
                id = stockLot.id,
                owner_id = stockLot.owner.id,
                owner_name = stockLot.owner.name,
                stock_code = stockLot.stock.code,
                stock_name = stockLot.stock.name,
                unit = stockLot.unit,
                quantity = quantity,
                is_nisa = stockLot.isNisa,
                status = stockLot.status,
                acquisition_price = acquisitionPrice,
                current_price = currentPrice,
                profit_loss = profitLoss,
                dividend = dividend
            )
        }.groupBy { it.is_nisa }
    }

    @Transactional
    fun deleteStockLot(lotId: Int) {
        stockLotRepository.deleteById(lotId)
    }

    @Transactional
    fun disposePartialStockLot(lotId: Int, unitsToDispose: Int) {
        val stockLot = stockLotRepository.findById(lotId)
            .orElseThrow { IllegalArgumentException("Invalid stock lot ID: $lotId") }

        if (unitsToDispose <= 0 || unitsToDispose >= stockLot.unit) {
            throw IllegalArgumentException("Invalid number of units to dispose")
        }

        stockLot.unit -= unitsToDispose
        stockLotRepository.save(stockLot)

        val stockInfo = yahooFinanceProvider.fetchStockInfo(stockLot.stock.code)
        val currentPrice = stockInfo?.price?.toBigDecimal() ?: BigDecimal.ZERO

        val disposalTransaction = Transaction(
            stockLot = stockLot,
            type = TransactionType.SELL,
            unit = unitsToDispose,
            price = currentPrice,
            fee = BigDecimal.ZERO,
            transaction_date = LocalDate.now()
        )
        transactionRepository.save(disposalTransaction)
    }
}
