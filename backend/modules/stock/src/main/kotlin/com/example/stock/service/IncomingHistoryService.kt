package com.example.stock.service

import com.example.stock.dto.IncomingHistoryAddDto
import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class IncomingHistoryService(
	private val incomingHistoryRepository: IncomingHistoryRepository,
	private val stockLotRepository: StockLotRepository,
	@Value("\${tax.stock.capital-gain-tax-rate:0.20315}")
	private val taxRate: BigDecimal
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

	/**
	 * DTOから配当履歴を作成します。
	 * @param dto 作成するためのDTO
	 * @return 保存されたエンティティ
	 */
	fun create(dto: IncomingHistoryAddDto): IncomingHistory {
		val stockLot = stockLotRepository.findById(dto.lotId)
			.orElseThrow { EntityNotFoundException("StockLot not found with id: ${dto.lotId}") }

		// NISA口座でない場合は税金を差し引く
		val netIncoming = if (!dto.isNisa) {
			dto.incoming.multiply(BigDecimal.ONE.subtract(taxRate))
		} else {
			dto.incoming
		}

		val incomingHistory = IncomingHistory(
			paymentDate = dto.paymentDate,
			stockLot = stockLot,
			incoming = netIncoming
		)

		return incomingHistoryRepository.save(incomingHistory)
	}

	/**
	 * 配当履歴を更新します。
	 * @param id 更新対象の配当履歴ID
	 * @param dto 更新内容を含むDTO
	 * @return 更新された配当履歴
	 * @throws NoSuchElementException 指定されたIDの配当履歴が存在しない場合
	 */
	fun update(id: Int, dto: IncomingHistoryAddDto): IncomingHistory {
		val existing = findById(id)
		
		// 既存のStockLotと異なる場合はエラー
		if (existing.stockLot?.id != dto.lotId) {
			throw IllegalArgumentException("Cannot change stockLot for existing income history")
		}

		// NISA口座でない場合は税金を差し引く
		val netIncoming = if (!dto.isNisa) {
			dto.incoming.multiply(BigDecimal.ONE.subtract(taxRate))
		} else {
			dto.incoming
		}

		val updated = existing.copy(
			paymentDate = dto.paymentDate,
			incoming = netIncoming
		)

		return incomingHistoryRepository.save(updated)
	}

	/**
	 * 配当履歴を削除します。
	 * @param id 削除対象の配当履歴ID
	 * @throws EntityNotFoundException 指定されたIDの配当履歴が見つからない場合
	 */
	fun delete(id: Int) {
		if (!incomingHistoryRepository.existsById(id)) {
			throw EntityNotFoundException("IncomingHistory not found with id: $id")
		}
		incomingHistoryRepository.deleteById(id)
	}
}
