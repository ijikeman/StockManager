package com.example.stock.service

import com.example.stock.model.SellTransaction
import com.example.stock.repository.SellTransactionRepository
import com.example.stock.repository.OwnerRepository
import com.example.stock.repository.StockRepository
import com.example.stock.repository.StockLotRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SellTransactionService(
	private val sellTransactionRepository: SellTransactionRepository,
	private val stockLotRepository: StockLotRepository
) {
    /**
     * すべての売却取引を取得します。
     * @return SellTransactionのリスト
     */
    fun findAll(): List<SellTransaction> {
        return sellTransactionRepository.findAll()
    }

    /**
     * BuyTransactionIDによって株式ロットを検索します。
     * @param buyTransactionId 所有者ID
     * @return SellTransactionのリスト
     */
    fun findByBuyTransactionId(buyTransactionId: Int): List<SellTransaction> {
        return sellTransactionRepository.findByBuyTransactionId(buyTransactionId)
    }

	/**
	 * 売却取引を作成します（StockLotの単元数は変更しません）。
	 * @param sellTransaction 作成する取引エンティティ
	 * @return 保存された取引エンティティ
	 */
	fun create(sellTransaction: SellTransaction): SellTransaction {
		return sellTransactionRepository.save(sellTransaction)
	}

	/**
	 * 売却取引を作成し、対応するStockLotの単元数を減らします。
	 * @param sellTransaction 作成する取引エンティティ
	 * @return 保存された取引エンティティ
	 * @throws IllegalArgumentException 単元数が不足している場合
	 */
	fun createAndUpdateStockLot(sellTransaction: SellTransaction): SellTransaction {
		val stockLot = sellTransaction.buyTransaction.stockLot
		val newUnit = stockLot.currentUnit - sellTransaction.unit
		if (newUnit < 0) {
			throw IllegalArgumentException("Not enough units in StockLot (id=${stockLot.id}) to sell: requested=${sellTransaction.unit}, available=${stockLot.currentUnit}")
		}
		// StockLotの単元数を減らして保存
		val updatedStockLot = stockLot.copy(currentUnit = newUnit)
		stockLotRepository.save(updatedStockLot)
		return sellTransactionRepository.save(sellTransaction)
	}
}
