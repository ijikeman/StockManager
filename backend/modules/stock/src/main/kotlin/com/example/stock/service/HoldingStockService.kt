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

/**
 * 保有株式に関するサービス
 */
@Service
@Transactional(readOnly = true)
class HoldingStockService(
    private val stockLotRepository: StockLotRepository,
    private val transactionRepository: TransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val yahooFinanceProvider: YahooFinanceProvider
) {

    /**
     * 保有株式の情報を取得する
     * @param ownerId 所有者のID
     * @return NISA口座かどうかでグループ化された保有株式の情報
     */
    fun getHoldingInfo(ownerId: Int?): Map<Boolean, List<HoldingInfoDTO>> {
        // 所有者のIDに基づいて保有株式を取得する
        val stockLots = if (ownerId != null) {
            stockLotRepository.findByOwnerIdAndStatus(ownerId, LotStatus.HOLDING)
        } else {
            stockLotRepository.findByStatus(LotStatus.HOLDING)
        }

        // 保有株式の情報をDTOに変換する
        return stockLots.map { stockLot ->
            // 購入取引の情報を取得する
            val transaction = transactionRepository.findByStockLotAndType(stockLot, TransactionType.BUY).firstOrNull()
            val acquisitionPrice = transaction?.price ?: BigDecimal.ZERO

            // 配当履歴を取得する
            val incomingHistories = incomingHistoryRepository.findByStockLot(stockLot)
            val dividend = incomingHistories.sumOf { it.incoming }

            // Yahoo Financeから株価情報を取得する
            val stockInfo = yahooFinanceProvider.fetchStockInfo(stockLot.stock.code)
            val currentPrice = stockInfo?.price?.toBigDecimal() ?: BigDecimal.ZERO

            // 数量と損益を計算する
            val quantity = stockLot.unit * stockLot.stock.minimalUnit
            val profitLoss = (currentPrice - acquisitionPrice).multiply(BigDecimal(quantity))

            // 保有株式の情報をDTOに設定する
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
                dividend = dividend,
                minimalUnit = stockLot.stock.minimalUnit
            )
        }.groupBy { it.is_nisa } // NISA口座かどうかでグループ化する
    }

    /**
     * 株式ロットを削除する
     * @param lotId ロットID
     */
    @Transactional
    fun deleteStockLot(lotId: Int) {
        stockLotRepository.deleteById(lotId)
    }

    /**
     * 株式ロットの一部を売却する
     * @param lotId ロットID
     * @param unitsToDispose 売却するユニット数
     */
    @Transactional
    fun disposePartialStockLot(lotId: Int, unitsToDispose: Int) {
        // ロットIDで株式ロットを取得する
        val stockLot = stockLotRepository.findById(lotId)
            .orElseThrow { IllegalArgumentException("Invalid stock lot ID: $lotId") }

        // 売却するユニット数が不正な場合は例外をスローする
        if (unitsToDispose <= 0 || unitsToDispose >= stockLot.unit) {
            throw IllegalArgumentException("Invalid number of units to dispose")
        }

        // ユニット数を減らして保存する
        stockLot.unit -= unitsToDispose
        stockLotRepository.save(stockLot)

        // Yahoo Financeから株価情報を取得する
        val stockInfo = yahooFinanceProvider.fetchStockInfo(stockLot.stock.code)
        val currentPrice = stockInfo?.price?.toBigDecimal() ?: BigDecimal.ZERO

        // 売却取引を作成して保存する
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