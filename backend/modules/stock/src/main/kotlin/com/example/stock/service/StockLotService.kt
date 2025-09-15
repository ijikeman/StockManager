package com.example.stock.service

import com.example.stock.model.LotStatus
import com.example.stock.model.Owner
import com.example.stock.model.Stock
import com.example.stock.model.StockLot
import com.example.stock.repository.StockLotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 株式ロットのサービス。
 * 株式ロットに関するビジネスロジックを処理します。
 */
@Service
@Transactional
class StockLotService(
    private val stockLotRepository: StockLotRepository
) {
    /**
     * 所有者IDとステータスによって株式ロットを検索します。
     *
     * @param ownerId 所有者ID
     * @param status ロットのステータス (オプション)
     * @return StockLotのリスト
     */
    // statusはオプションパラメータとして渡される
    fun findByOwnerId(ownerId: Int, status: String?): List<StockLot> {
        return if (status != null) {
            // statusが指定されていれば、新しいメソッドを呼び出す
            val lotStatus = LotStatus.valueOf(status.uppercase())
            stockLotRepository.findByOwnerIdAndStatus(ownerId, lotStatus)
        } else {
            // statusがなければ、既存のメソッドを呼び出す
            stockLotRepository.findByOwnerId(ownerId)
        }
    }

    /**
     * 複数の株式ロットを作成します。株数は100の倍数でなければなりません。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param isNisa NISA口座かどうか
     * @param totalQuantity 合計株数
     * @return 作成されたStockLotのリスト
     * @throws IllegalArgumentException 株数が100の倍数でない場合
     */
    fun createStockLots(owner: Owner, stock: Stock, isNisa: Boolean, totalQuantity: Int): List<StockLot> {
        if (totalQuantity % 100 != 0) {
            throw IllegalArgumentException("Quantity must be a multiple of 100")
        }
        val lots = mutableListOf<StockLot>()
        for (i in 0 until totalQuantity / 100) {
            val stockLot = StockLot(
                owner = owner,
                stock = stock,
                isNisa = isNisa,
                quantity = 100
            )
            lots.add(stockLotRepository.save(stockLot))
        }
        return lots
    }

    /**
     * 単一の株式ロットを作成します。
     *
     * @param owner 所有者
     * @param stock 株式
     * @param isNisa NISA口座かどうか
     * @param quantity 株数
     * @return 作成されたStockLot
     */
    fun createSingleStockLot(owner: Owner, stock: Stock, isNisa: Boolean, quantity: Int): StockLot {
        val stockLot = StockLot(
            owner = owner,
            stock = stock,
            isNisa = isNisa,
            quantity = quantity
        )
        return stockLotRepository.save(stockLot)
    }
}
