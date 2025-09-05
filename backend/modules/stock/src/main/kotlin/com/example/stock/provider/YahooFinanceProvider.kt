package com.example.stock.provider

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.regex.Pattern

@Component
class YahooFinanceProvider(
    @Value("\${yahoo.finance.request.delay.millis:1000}")
    private val requestDelayMillis: Long
) : FinanceProvider {

    companion object {
        private const val BASE_URL = "https://finance.yahoo.co.jp/quote"
        private const val PRELOADED_STATE_PREFIX = "window.__PRELOADED_STATE__ = "
    }

    private val objectMapper = ObjectMapper()

    override fun fetchStockInfo(code: String): StockInfo? {
        // リクエスト間の遅延を挿入して、サーバーへの負荷を軽減します。
        Thread.sleep(requestDelayMillis)

        return try {
            val url = "$BASE_URL/$code.T"
            val doc = Jsoup.connect(url).get()
            val preloadedState = getPreloadedState(doc)

            val price = extractPrice(preloadedState)
            val dividend = extractDividend(preloadedState)
            val earningsDate = extractEarningsDate(preloadedState)

            StockInfo(price, dividend, earningsDate)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun fetchStockName(code: String): String? {
        // リクエスト間の遅延を挿入します。
        Thread.sleep(requestDelayMillis)

        return try {
            val url = "$BASE_URL/$code.T"
            val doc = Jsoup.connect(url).get()
            val preloadedState = getPreloadedState(doc)
            extractName(preloadedState)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getPreloadedState(doc: org.jsoup.nodes.Document): JsonNode? {
        val scriptElements = doc.getElementsByTag("script")
        val script = scriptElements.find { it.html().trim().startsWith(PRELOADED_STATE_PREFIX) }
            ?: return null
        val jsonText = script.html().trim().removePrefix(PRELOADED_STATE_PREFIX)
        return objectMapper.readTree(jsonText)
    }

    private fun extractName(jsonNode: JsonNode?): String? {
        return jsonNode?.at("/mainStocksPriceBoard/priceBoard/name")?.asText()
    }

    private fun extractPrice(jsonNode: JsonNode?): Double? {
        return jsonNode?.at("/mainStocksPriceBoard/priceBoard/price")?.asDouble()
    }

    private fun extractDividend(jsonNode: JsonNode?): Double? {
        // "dps" in referenceIndex seems to be "Dividend Per Share"
        return jsonNode?.at("/mainStocksDetail/referenceIndex/dps")?.asText()?.toDoubleOrNull()
    }

    private fun extractEarningsDate(jsonNode: JsonNode?): LocalDate? {
        val earningsText = jsonNode?.at("/mainStocksPressReleaseSchedule/pressReleaseScheduleMessage")?.asText()
            ?: return null

        val pattern = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日")
        val matcher = pattern.matcher(earningsText)

        return if (matcher.find()) {
            val year = matcher.group(1).toInt()
            val month = matcher.group(2).toInt()
            val day = matcher.group(3).toInt()
            LocalDate.of(year, month, day)
        } else {
            null
        }
    }
}
