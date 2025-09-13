package com.example.stock.service

import com.example.stock.model.IncomeHistory
import com.example.stock.repository.IncomeHistoryRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.HoldingRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

data class IncomeHistoryRequest(
    val date: LocalDate,
    val stock_code: String,
    val amount: Double
)

/**
 * 収益履歴([IncomeHistory])に関する基本的なビジネスロジックを提供するサービスクラス
 * @param incomeHistoryRepository 収益履歴リポジトリ
 */
@Service
class IncomeHistoryService(
    private val incomeHistoryRepository: IncomeHistoryRepository,
    private val stockRepository: StockRepository,
    private val holdingRepository: HoldingRepository
    ) {

    /**
     * すべての収益履歴を取得します。
     * @return 収益履歴のリスト
     */
    fun findAllIncomeHistories(): List<IncomeHistory> {
        return incomeHistoryRepository.findAll()
    }

    /**
     * IDで収益履歴を取得します。
     * @return 収益履歴
     */
    fun findIncomeHistoryById(id: Int): IncomeHistory {
        return incomeHistoryRepository.findById(id).orElseThrow { RuntimeException("Income history not found with id: $id") }
    }

    /**
     * 指定された保有IDに紐づくすべての収益履歴を取得します。
     * @param holdingId 検索対象の保有ID
     * @return 見つかった収益履歴のリスト
     */
    fun findIncomeHistoriesByHoldingId(holdingId: Int): List<IncomeHistory> {
        return incomeHistoryRepository.findByHoldingId(holdingId)
    }

    /**
     * 収益履歴を保存します。
     * @param incomeHistory 保存する収益履歴オブジェクト
     * @return 保存された収益履歴オブジェクト
     */
    @Transactional
    fun createIncomeHistory(request: IncomeHistoryRequest): IncomeHistory {
        val stock = stockRepository.findByCode(request.stock_code) ?: throw RuntimeException("Stock not found with code: ${request.stock_code}")
        // １銘柄は１オーナーが１つだけ保有している前提
        val holding = holdingRepository.findByStockId(stock.id).firstOrNull() ?: throw RuntimeException("Holding not found for stock code: ${request.stock_code}")

        val incomeHistory = IncomeHistory(
            holding = holding,
            income_type = "dividend", // フロントから指定がないため固定
            amount = request.amount,
            date = request.date
        )
        return incomeHistoryRepository.save(incomeHistory)
    }

    /**
     * 収益履歴を更新します。
     * @param id 更新対象のID
     * @param request 更新内容
     * @return 更新された収益履歴オブジェクト
     */
    @Transactional
    fun updateIncomeHistory(id: Int, request: IncomeHistoryRequest): IncomeHistory {
        val existingHistory = findIncomeHistoryById(id)
        val stock = stockRepository.findByCode(request.stock_code) ?: throw RuntimeException("Stock not found with code: ${request.stock_code}")
        val holding = holdingRepository.findByStockId(stock.id).firstOrNull() ?: throw RuntimeException("Holding not found for stock code: ${request.stock_code}")

        val updatedHistory = existingHistory.copy(
            holding = holding,
            amount = request.amount,
            date = request.date
        )
        return incomeHistoryRepository.save(updatedHistory)
    }

    /**
     * 収益履歴を削除します。
     * @param id 削除対象のID
     */
    fun deleteIncomeHistory(id: Int) {
        incomeHistoryRepository.deleteById(id)
    }
}
