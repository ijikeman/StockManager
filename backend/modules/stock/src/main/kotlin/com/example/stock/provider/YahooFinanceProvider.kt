package com.example.stock.provider

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
    }

    override fun fetchStockInfo(code: String): StockInfo? {
        // リクエスト間の遅延を挿入して、サーバーへの負荷を軽減します。
        Thread.sleep(requestDelayMillis)

        return try {
            val url = "$BASE_URL/$code.T"
            val doc = Jsoup.connect(url).get()

            val price = extractPrice(doc)
            val dividend = extractDividend(doc)
            val earningsDate = extractEarningsDate(doc)

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
            extractName(doc)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractName(doc: org.jsoup.nodes.Document): String? {
        return doc.selectFirst("""div[data-testid="stock-name"]""")?.text()
    }

    private fun extractPrice(doc: org.jsoup.nodes.Document): Double? {
        val priceText = doc.selectFirst("""div[data-testid="stock-price"]""")?.text()?.replace(",", "")
        return priceText?.toDoubleOrNull()
    }

    private fun extractDividend(doc: org.jsoup.nodes.Document): Double? {
        val dividendElement = doc.select("dt").find { it.text().contains("配当利回り（会社予想）") }
        val valueText = dividendElement?.nextElementSibling()?.text()?.replace("%", "")
        return valueText?.toDoubleOrNull()
    }

    private fun extractEarningsDate(doc: org.jsoup.nodes.Document): LocalDate? {
        val earningsText = doc.select("p:contains(直近の決算発表日は)").text()
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
