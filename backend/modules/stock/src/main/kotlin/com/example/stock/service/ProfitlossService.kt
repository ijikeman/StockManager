package com.example.stock.service

import com.example.stock.dto.ProfitlossStockLotDto
import com.example.stock.dto.ProfitlossDto
import com.example.stock.repository.BuyTransactionRepository
import com.example.stock.repository.SellTransactionRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.model.IncomingHistory
import com.example.stock.model.BenefitHistory
import org.springframework.stereotype.Service
import java.math.BigDecimal

/**
 * 損益情報に関するビジネスロジックを処理するサービス。
 */
@Service
class ProfitlossService(
    private val stockLotService: StockLotService,
    private val buyTransactionRepository: BuyTransactionRepository,
    private val sellTransactionRepository: SellTransactionRepository,
    private val incomingHistoryRepository: IncomingHistoryRepository,
    private val benefitHistoryRepository: BenefitHistoryRepository
) {
    
    companion object {
        /**
         * 株式譲渡益課税率 (20% 所得税 + 0.315% 復興特別所得税 = 20.315%)
         * NISA口座でない場合に適用される税率
         */
        private const val TAX_STOCK_CAPITALGAINSTAXRATE = 0.20315
        
        /**
         * 税引き後の割合 (1 - TAX_STOCK_CAPITALGAINSTAXRATE = 0.79685)
         */
        private val AFTER_TAX_RATIO = BigDecimal.ONE - BigDecimal.valueOf(TAX_STOCK_CAPITALGAINSTAXRATE)
    }

    /**
     * 株式ロットから損益情報を取得します。
     * ownerIdに紐づく全stocklotを検索し、関連buytransactionを検索し、
     * さらにselltransactionを検索し、売却損益を計算する。
     * @param ownerId 所有者ID（オプション）。指定された場合は、その所有者の株式ロットのみを取得します。
     * @return 損益情報のリスト
     */
    fun getProfitStockLotLoss(ownerId: Int? = null): List<ProfitlossStockLotDto> {
        // 対象となる株式ロットを取得
        val stockLots = if (ownerId != null) {
            // 指定された所有者のみ
            stockLotService.findByOwnerId(ownerId)
        } else {
            // 全ての所有者
            stockLotService.findAll()
        }.filter { it.currentUnit > 0 } // 現在の単元数が0より大きい株式ロットのみ対象

        // --- start: 購入取引を一括取得 --- //
        // パフォーマンス最適化：N+1クエリ問題を完全に回避(IN句による一括取得を実施する)為、StockLotIdを一覧化
        val stockLotIds = stockLots.map { it.id }
        
         // 全ての株式ロットID(StockLotIds)に対して購入取引を一括取得（1回のクエリで全て取得）
        // Map<StockLotId, List<BuyTransaction>>の形でグループ化
        val buyTransactionsMap = if (stockLotIds.isNotEmpty()) {
           buyTransactionRepository.findByStockLotIdIn(stockLotIds)
               .groupBy { it.stockLot.id }
        } else {
            emptyMap()
        }
        // --- end: 購入取引を一括取得 --- //

        // --- start: 配当金履歴の一括取得と合計計算 --- //
        // 配当金履歴の一括取得と合計計算する為、buyTransaction.IdをbuyTransactionIdsとして一覧化
        val buyTransactionIds = buyTransactionsMap.values.flatten().mapNotNull { it.id }

        // 全ての株式ロットに対して配当金履歴を一括取得（1回のクエリで全て取得）
        // Map<StockLotId, List<IncomingHistory>>の形でグループ化
        val incomingHistoriesMap = if (stockLotIds.isNotEmpty()) {
            incomingHistoryRepository.findByStockLotIdIn(stockLotIds)
                .filter { it.stockLot != null } // stockLot が null でないものをフィルタリング
                .groupBy({ it.stockLot!!.id }, { it }) // 非nullアサーション (!!) を使用して id を取得
        } else {
            emptyMap<Int, List<IncomingHistory>>()
        }

        // 株式ロットごとの総配当金額を計算
        // nullの場合は0として扱い、安全に合計を算出
        val incomingTotalsMap: Map<Int, BigDecimal> = incomingHistoriesMap.mapValues { (_, incomes) ->
            incomes.fold(BigDecimal.ZERO) { total, incomingHistory ->
            total + (incomingHistory.incoming ?: BigDecimal.ZERO) // nullの場合は0として扱う
            }
        }
        // --- end: 配当金履歴の一括取得と合計計算 --- //

        // --- start: 株主優待履歴の一括取得と合計計算 --- //
        // 全ての株式ロットに対して株主優待履歴を一括取得（1回のクエリで全て取得）
        // Map<StockLotId, List<BenefitHistory>>の形でグループ化
        val benefitHistoriesMap = if (stockLotIds.isNotEmpty()) {
            benefitHistoryRepository.findByStockLotIdIn(stockLotIds)
                .filter { it.stockLot != null } // stockLot が null でないものをフィルタリング
                .groupBy({ it.stockLot!!.id }, { it }) // 非nullアサーション (!!) を使用して id を取得
        } else {
            emptyMap<Int, List<BenefitHistory>>()
        }

        // 株式ロットごとの総株主優待金額を計算
        // nullの場合は0として扱い、安全に合計を算出
        val benefitTotalsMap: Map<Int, BigDecimal> = benefitHistoriesMap.mapValues { (_, benefits) ->
            benefits.fold(BigDecimal.ZERO) { total, benefitHistory ->
                total + (benefitHistory.benefit ?: BigDecimal.ZERO) // nullの場合は0として扱う
            }
        }
        // --- end: 株主優待履歴の一括取得と合計計算 --- //

        // --- start: 損益情報DTOのリストを作成 --- //
        return stockLots.map { stockLot ->
            // 該当する株式ロットの購入取引を取得
            val buyTransactions = buyTransactionsMap[stockLot.id] ?: emptyList()

            // NISA判定：全ての購入取引がNISA口座での取引の場合のみisNisa=trueとする(stockLot=buyTransactionは1:1の関係のため、すべてのbuyTransactionは同じisNisa値を持つ)
            val isNisa = buyTransactions.isNotEmpty() && buyTransactions.all { it.isNisa }

            // 株式ロット全体の配当金額を集計
            // 事前に一括取得した配当金履歴から該当する株式ロットの合計を算出
            var totalIncoming = incomingTotalsMap[stockLot.id] ?: BigDecimal.ZERO
            // 税金処理：NISA口座でない場合は配当金に税金（20.315%）を適用
            if (!isNisa) {
                totalIncoming = totalIncoming.multiply(AFTER_TAX_RATIO)
            }

            // 株式ロット全体の株主優待金額を集計  
            // 事前に一括取得した優待履歴から該当する株式ロットの合計を算出
            val totalBenefit = benefitTotalsMap[stockLot.id] ?: BigDecimal.ZERO

            // 最初の購入取引（最も古い取引日）を基準価格として使用
            val firstBuyTransaction = buyTransactions.minByOrNull { it.transactionDate }

            // 評価損益の計算（含み損益）
            // 現在価格と保有単元数が存在する場合のみ計算
            val evaluationProfitloss = if (stockLot.stock.currentPrice != null && stockLot.currentUnit != null) {
                // 基準となる購入価格（初回購入価格）
                val purchasePrice = firstBuyTransaction?.price ?: BigDecimal.ZERO
                val currentPriceBD = BigDecimal.valueOf(stockLot.stock.currentPrice)
                
                // 損益 = (現在価格 - 購入価格) × 保有単元数 × 最小単元(.multiplyはBigDecimalにおける乗算)
                val profitloss = (currentPriceBD - purchasePrice)
                    .multiply(BigDecimal.valueOf(stockLot.currentUnit.toLong()))
                    .multiply(BigDecimal.valueOf(stockLot.stock.minimalUnit.toLong()))
                
                // NISA口座でない場合は株式譲渡益税（20.315%）を適用
                if (!isNisa) {
                    profitloss.multiply(AFTER_TAX_RATIO)
                } else {
                    profitloss
                }
            } else {
                null
            }

            // 損益情報DTOを作成して返却
            ProfitlossStockLotDto(
                stockCode = stockLot.stock.code,                        // 株式コード
                stockName = stockLot.stock.name,                        // 株式名
                minimalUnit = stockLot.stock.minimalUnit,               // 最小単元数
                purchasePrice = firstBuyTransaction?.price?.toDouble() ?: 0.0, // 購入価格（初回購入価格）
                currentPrice = stockLot.stock.currentPrice,             // 現在価格
                currentUnit = stockLot.currentUnit,                     // 現在保有単元数
                totalIncoming = totalIncoming,                          // 総配当金（税引き後）
                totalBenefit = totalBenefit,                           // 総株主優待金
                evaluationProfitloss = evaluationProfitloss,           // 評価損益（含み損益、税引き後）
                buyTransactionDate = firstBuyTransaction?.transactionDate, // 初回購入日
                ownerName = stockLot.owner.name,                       // 所有者名
                isNisa = isNisa                                        // NISA口座フラグ
            )
        }
    }

    /**
     * 売却取引から損益情報を取得します。
     * ownerIdに紐づく全stocklotを検索し、関連buytransactionを検索し、
     * さらにselltransactionを検索し、売却損益を計算する。
     * @param ownerId 所有者ID（オプション）。指定された場合は、その所有者の株式ロットのみを取得します。
     * @return 損益情報のリスト
     */
    fun getSellTransactionProfitloss(ownerId: Int? = null): List<ProfitlossDto> {
        // 対象となる株式ロットを取得（指定された所有者のみまたは全て）
        // 売却取引が存在する場合も、保有数が0のロットも含めて取得
        val stockLots = if (ownerId != null) {
            stockLotService.findByOwnerId(ownerId)
        } else {
            stockLotService.findAll()
        }

        // --- start: 購入取引を一括取得 --- //
        // パフォーマンス最適化：N+1クエリ問題を完全に回避するため、IN句による一括取得を実施
        val stockLotIds = stockLots.map { it.id }
        
        // 全ての株式ロットIDに対して購入取引を一括取得（1回のクエリで全て取得）
        // Map<StockLotId, List<BuyTransaction>>の形でグループ化
        val buyTransactionsMap = if (stockLotIds.isNotEmpty()) {
           buyTransactionRepository.findByStockLotIdIn(stockLotIds)
               .groupBy { it.stockLot.id }
        } else {
            emptyMap()
        }
        // --- end: 購入取引を一括取得 --- //

        // --- start: 売却取引の一括取得 --- //
        // 全ての購入取引IDに対して売却取引を一括取得（1回のクエリで全て取得）
        val buyTransactionIds = buyTransactionsMap.values.flatten().mapNotNull { it.id }
        // Map<BuyTransactionId, List<SellTransaction>>の形でグループ化
        val sellTransactionsMap = if (buyTransactionIds.isNotEmpty()) {
               sellTransactionRepository.findByBuyTransactionIdIn(buyTransactionIds)
                    .filter { it.buyTransaction != null } // buyTransaction が null でないものをフィルタリング
                    .groupBy { it.buyTransaction!!.id } // 非nullアサーション (!!)
        } else {
            emptyMap()
        }
        // --- end: 売却取引の一括取得 --- //

        // --- start: 配当金履歴の合計計算 --- //
        // 売却取引に関連する配当金履歴の一括取得
        val sellTransactionIds = sellTransactionsMap.values.flatten().mapNotNull { it.id }

        // 売却取引に関連する配当金履歴を一括取得（1回のクエリで全て取得）
        // Map<SellTransactionId, List<IncomingHistory>>の形でグループ化
        val incomingHistoriesMap = if (sellTransactionIds.isNotEmpty()) {
            incomingHistoryRepository.findBySellTransactionIdIn(sellTransactionIds)
                .filter { it.sellTransaction != null } // sellTransaction が null でないものをフィルタリング
                .groupBy({ it.sellTransaction!!.id }, { it }) // 非nullアサーション (!!) を使用して id を取得
        } else {
            emptyMap<Int, List<IncomingHistory>>()
        }

        // 売却取引ごとの総配当金額を計算
        // nullの場合は0として扱い、安全に合計を算出
        val incomingTotalsMap: Map<Int, BigDecimal> = incomingHistoriesMap.mapValues { (_, incomes) ->
            incomes.fold(BigDecimal.ZERO) { total, incomingHistory ->
            total + (incomingHistory.incoming ?: BigDecimal.ZERO) // nullの場合は0として扱う
            }
        }
        // --- end: 配当金履歴の合計計算 --- //

        // --- start: 株主優待履歴の一括取得と合計計算 --- //
        // 全ての売却取引に対して株主優待履歴を一括取得（1回のクエリで全て取得）
        // Map<SellTransactionId, List<BenefitHistory>>の形でグループ化
        val benefitHistoriesMap = if (sellTransactionIds.isNotEmpty()) {
            benefitHistoryRepository.findBySellTransactionIdIn(sellTransactionIds)
                .filter { it.sellTransaction != null } // sellTransaction が null でないものをフィルタリング
                .groupBy({ it.sellTransaction!!.id }, { it }) // 非nullアサーション (!!) を使用して id を取得
        } else {
            emptyMap<Int, List<BenefitHistory>>()
        }

        // 売却取引ごとの総株主優待金額を計算
        // nullの場合は0として扱い、安全に合計を算出
        val benefitTotalsMap: Map<Int, BigDecimal> = benefitHistoriesMap.mapValues { (_, benefits) ->
            benefits.fold(BigDecimal.ZERO) { total, benefitHistory ->
                total + (benefitHistory.benefit ?: BigDecimal.ZERO) // nullの場合は0として扱う
            }
        }
        // --- end: 株主優待履歴の一括取得と合計計算 --- //

        // --- start: 売却損益情報DTOのリストを作成 --- //
        // 各売却取引ごとに1つのDTOを作成（入れ子ループで処理）
        val result = mutableListOf<ProfitlossDto>()
        
        // 株式ロットごとに処理
        stockLots.forEach { stockLot ->
            val buyTransactions = buyTransactionsMap[stockLot.id] ?: emptyList()
            
            // 購入取引ごとに処理
            buyTransactions.forEach { buyTransaction ->
                val sellTransactions = sellTransactionsMap[buyTransaction.id] ?: emptyList()
                
                // 売却取引がある場合のみ、各売却取引ごとにDTOを作成
                sellTransactions.forEach { sellTransaction ->
                    // 基本損益計算：(売却価格 - 購入価格) × 売却単元数 × 最小単元数 - 手数料
                    var profitLoss = ((sellTransaction.price - buyTransaction.price) * 
                                    sellTransaction.unit.toBigDecimal() * 
                                    stockLot.stock.minimalUnit.toBigDecimal()) - 
                                    buyTransaction.fee - sellTransaction.fee
                    
                    // 売却取引に対応する配当金と株主優待金を取得
                    var totalIncoming = incomingTotalsMap[sellTransaction.id] ?: BigDecimal.ZERO
                    val totalBenefit = benefitTotalsMap[sellTransaction.id] ?: BigDecimal.ZERO
                    
                    // 税金処理：NISA口座でない場合は配当金と株価差益に税金を適用
                    if (!buyTransaction.isNisa) {
                        // 配当金に税金（20.315%）を適用
                        totalIncoming = totalIncoming.multiply(AFTER_TAX_RATIO)
                        
                        // 売却益にのみ税金を適用（売却損の場合は税金の恩恵なし）
                        if (profitLoss > BigDecimal.ZERO) {
                            profitLoss = profitLoss.multiply(AFTER_TAX_RATIO)
                        }
                    }
                    
                    // 売却損益DTOを作成してリストに追加
                    result.add(ProfitlossDto(
                        stockCode = stockLot.stock.code,                    // 株式コード
                        stockName = stockLot.stock.name,                    // 株式名
                        minimalUnit = stockLot.stock.minimalUnit,           // 最小単元数
                        purchasePrice = buyTransaction.price.toDouble(),    // 購入価格
                        sellPrice = sellTransaction.price.toDouble(),       // 売却価格
                        sellUnit = sellTransaction.unit,                    // 売却単元数
                        totalIncoming = totalIncoming.toDouble(),           // 総配当金（税引き後）
                        totalBenefit = totalBenefit.toDouble(),            // 総株主優待金
                        profitLoss = profitLoss,                            // 最終損益（手数料控除後、税引き後）
                        buyTransactionDate = buyTransaction.transactionDate, // 購入日
                        sellTransactionDate = sellTransaction.transactionDate, // 売却日
                        ownerName = stockLot.owner.name,                   // 所有者名
                        isNisa = buyTransaction.isNisa                     // NISA口座フラグ
                    ))
                }
            }
        }
        
        // 作成した売却損益DTOのリストを返却
        return result
    }
}