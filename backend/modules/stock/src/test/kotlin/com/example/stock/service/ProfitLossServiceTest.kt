package com.example.stock.service

import com.example.stock.model.*
import com.example.stock.provider.FinanceProvider
import com.example.stock.provider.StockInfo
import com.example.stock.repository.BenefitHistoryRepository
import com.example.stock.repository.IncomingHistoryRepository
import com.example.stock.repository.StockLotRepository
import com.example.stock.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class ProfitLossServiceTest {

    // ProfitLossServiceの単体テスト

    @InjectMocks
    private lateinit var profitLossService: ProfitLossService

    @Mock
    private lateinit var stockLotRepository: StockLotRepository

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    @Mock
    private lateinit var incomingHistoryRepository: IncomingHistoryRepository

    @Mock
    private lateinit var benefitHistoryRepository: BenefitHistoryRepository

    @Mock
    private lateinit var financeProvider: FinanceProvider

    @Test
    fun `calculateTotalProfitLoss should return correct summary`() {
        // Arrange（テストデータの準備）
        // テスト用のオーナー・株式・ロットを作成
        val owner = Owner(id = 1, name = "Test Owner")
        // 50株を1単元とする
        val stock = Stock(id = 1, code = "1234", name = "Test Stock", current_price = 1500.0, minimalUnit = 50)
        // 2単元（100株）保有
        val lot = StockLot(id = 1, owner = owner, stock = stock, unit = 2, status = LotStatus.HOLDING)

        // 売買トランザクションを作成
        // 2単元（100株）購入
        val buyTransaction = Transaction(
            id = 1, stockLot = lot, type = TransactionType.BUY, unit = 2,
            price = BigDecimal("1000.0"), tax = BigDecimal.ZERO, transaction_date = LocalDate.now()
        )
        // 1単元（50株）売却
        val sellTransaction = Transaction(
            id = 2, stockLot = lot, type = TransactionType.SELL, unit = 1,
            price = BigDecimal("1200.0"), tax = BigDecimal("500.0"), transaction_date = LocalDate.now()
        )

        // 配当・利益データを作成
        val incoming = IncomingHistory(id = 1, stockLot = lot, incoming = BigDecimal("10000.0"), payment_date = LocalDate.now())
        val benefit = BenefitHistory(id = 1, stockLot = lot, benefit = BigDecimal("5000.0"))

        // モックの戻り値を設定
        `when`(stockLotRepository.findByOwnerId(owner.id)).thenReturn(listOf(lot))
        `when`(transactionRepository.findByStockLotId(lot.id)).thenReturn(listOf(buyTransaction, sellTransaction))
        `when`(incomingHistoryRepository.findByStockLotId(lot.id)).thenReturn(listOf(incoming))
        `when`(benefitHistoryRepository.findByStockLotId(lot.id)).thenReturn(listOf(benefit))
        `when`(financeProvider.fetchStockInfo(stock.code)).thenReturn(StockInfo(price = 1500.0, dividend = 0.0, earnings_date = null))


        // Act（テスト対象メソッドの実行）
        val summary = profitLossService.calculateTotalProfitLoss(owner.id)

        // Assert（結果の検証）
        // 実現損益 = (1200 - 1000) * 50 - 500 = 9500
        assertEquals(BigDecimal("9500.0"), summary.realizedPl)
        // 含み損益 = (1500 - 1000) * (2 * 50 - 50) = 25000
        assertEquals(BigDecimal("25000.0"), summary.unrealizedPl)
        // 収入 = 10000 + 5000 = 15000
        assertEquals(BigDecimal("15000.0"), summary.income)
        // 合計損益 = 9500 + 25000 + 15000 = 49500
        assertEquals(BigDecimal("49500.0"), summary.totalPl)
    }
}
