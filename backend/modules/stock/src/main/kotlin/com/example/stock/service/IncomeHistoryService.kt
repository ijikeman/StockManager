package com.example.stock.service

import com.example.stock.model.IncomeHistory
import com.example.stock.repository.IncomeHistoryRepository
import org.springframework.stereotype.Service

/**
 * 収益履歴([IncomeHistory])に関する基本的なビジネスロジックを提供するサービスクラス
 * @param incomeHistoryRepository 収益履歴リポジトリ
 */
@Service
class IncomeHistoryService(private val incomeHistoryRepository: IncomeHistoryRepository) {

    /**
     * すべての収益履歴を取得します。
     * @return 収益履歴のリスト
     */
    fun findAllIncomeHistories(): List<IncomeHistory> {
        return incomeHistoryRepository.findAll()
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
    fun saveIncomeHistory(incomeHistory: IncomeHistory): IncomeHistory {
        return incomeHistoryRepository.save(incomeHistory)
    }
}
