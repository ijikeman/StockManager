package com.example.stock.service

import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class IncomingHistoryService(
	private val incomingHistoryRepository: IncomingHistoryRepository
) {

	/**
	 * 全ての配当履歴を取得します。
	 * @return 配当履歴のリスト
	 */
	fun findAll(): List<IncomingHistory> {
		return incomingHistoryRepository.findAll()
	}

	/**
	 * IDで配当履歴を検索します。
	 * @param id 検索する配当履歴のID
	 * @return 見つかった配当履歴
	 * @throws EntityNotFoundException 指定されたIDの配当履歴が見つからない場合
	 */
    fun findById(id: Int): IncomingHistory {
        return incomingHistoryRepository.findById(id)
			.orElseThrow { EntityNotFoundException("IncomingHistory not found with id: $id") }
    }

	/**
	 * 配当履歴を作成します。
	 * @param incomingHistory 作成するエンティティ
	 * @return 保存されたエンティティ
	 */
	fun create(incomingHistory: IncomingHistory): IncomingHistory {
		// IDが0またはnullであることを保証して新規作成する
		require(incomingHistory.id == 0) { "ID must be 0 for a new entity." }
		return incomingHistoryRepository.save(incomingHistory)
	}
}
