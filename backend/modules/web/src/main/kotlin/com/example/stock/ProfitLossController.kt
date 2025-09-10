package com.example.stock

import com.example.stock.service.ProfitLossService
import com.example.stock.service.ProfitLossSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 損益計算に関するAPIリクエストを処理するコントローラー
 * @param profitLossService 損益計算サービス
 */
@RestController
@RequestMapping("/api/pl")
class ProfitLossController(private val profitLossService: ProfitLossService) {

    /**
     * 指定されたオーナーの損益サマリーを取得します。
     *
     * @param ownerId 損益を取得するオーナーのID
     * @return 損益サマリー ([ProfitLossSummary])
     */
    @GetMapping("/{ownerId}")
    fun getProfitLossSummary(@PathVariable ownerId: Int): ProfitLossSummary {
        return profitLossService.calculateTotalProfitLoss(ownerId)
    }
}
