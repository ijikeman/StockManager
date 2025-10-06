import com.example.stock.model.IncomingHistory
import com.example.stock.repository.IncomingHistoryRepository
@org.springframework.stereotype.Service
@org.springframework.transaction.annotation.Transactional

class IncomingHistoryService(
	private val incomingHistoryRepository: IncomingHistoryRepository
) {
	/**
	 * 配当履歴を作成します。
	 * @param incomingHistory 作成するエンティティ
	 * @return 保存されたエンティティ
	 */
	fun create(incomingHistory: IncomingHistory): IncomingHistory {
		return incomingHistoryRepository.save(incomingHistory)
	}
}
