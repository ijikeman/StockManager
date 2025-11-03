package com.example.stock.service

import com.example.stock.dto.BenefitHistoryAddDto
import com.example.stock.model.BenefitHistory
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class BenefitHistoryService(
	private val benefitHistoryRepository: BenefitHistoryRepository,
	private val stockLotRepository: StockLotRepository
) {

	/**
	 * 全ての優待履歴を取得します。
	 * @return 優待履歴のリスト
	 */
	fun findAll(): List<BenefitHistory> {
		return benefitHistoryRepository.findAll()
	}

	/**
	 * IDで優待履歴を検索します。
	 * @param id 検索する優待履歴のID
	 * @return 見つかった優待履歴
	 * @throws EntityNotFoundException 指定されたIDの優待履歴が見つからない場合
	 */
    fun findById(id: Int): BenefitHistory {
        return benefitHistoryRepository.findById(id)
			.orElseThrow { EntityNotFoundException("BenefitHistory not found with id: $id") }
    }

	/**
	 * 優待履歴を作成します。
	 * @param benefitHistory 作成するエンティティ
	 * @return 保存されたエンティティ
	 */
	fun create(benefitHistory: BenefitHistory): BenefitHistory {
		// IDが0またはnullであることを保証して新規作成する
		require(benefitHistory.id == 0) { "ID must be 0 for a new entity." }
		return benefitHistoryRepository.save(benefitHistory)
	}

	/**
	 * DTOから優待履歴を作成します。
	 * @param dto 作成するためのDTO
	 * @return 保存されたエンティティ
	 */
	fun create(dto: BenefitHistoryAddDto): BenefitHistory {
		val stockLot = stockLotRepository.findById(dto.lotId)
			.orElseThrow { EntityNotFoundException("StockLot not found with id: ${dto.lotId}") }

		val benefitHistory = BenefitHistory(
			paymentDate = dto.paymentDate,
			stockLot = stockLot,
			benefit = dto.benefit
		)

		return benefitHistoryRepository.save(benefitHistory)
	}

	/**
	 * 優待履歴を更新します。
	 * @param id 更新対象の優待履歴ID
	 * @param dto 更新内容を含むDTO
	 * @return 更新された優待履歴
	 * @throws NoSuchElementException 指定されたIDの優待履歴が存在しない場合
	 */
	fun update(id: Int, dto: BenefitHistoryAddDto): BenefitHistory {
		val existing = findById(id)
		
		// 既存のStockLotと異なる場合はエラー
		if (existing.stockLot?.id != dto.lotId) {
			throw IllegalArgumentException("Cannot change stockLot for existing benefit history")
		}

		val updated = existing.copy(
			paymentDate = dto.paymentDate,
			benefit = dto.benefit
		)

		return benefitHistoryRepository.save(updated)
	}

	/**
	 * 優待履歴を削除します。
	 * @param id 削除対象の優待履歴ID
	 * @throws EntityNotFoundException 指定されたIDの優待履歴が見つからない場合
	 */
	fun delete(id: Int) {
		if (!benefitHistoryRepository.existsById(id)) {
			throw EntityNotFoundException("BenefitHistory not found with id: $id")
		}
		benefitHistoryRepository.deleteById(id)
	}
}
