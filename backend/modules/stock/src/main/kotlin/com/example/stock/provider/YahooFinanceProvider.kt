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
            val doc = Jsoup.connect(url).get() // URLリクエストを行いデータを取得

            // docから各データを取得
            val price = extractPrice(doc)
            val incoming = extractIncoming(doc)
            val earnings_date = extractEarningsDate(doc)

            // 取得したデータをStockInfoに格納しreturnする
            StockInfo(price, incoming, earnings_date)
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
        val title = doc.title()
        val stockName = title.split("【")[0]
        return if (stockName.isNotEmpty()) {
            stockName
        } else {
            null
        }
    }

    private fun extractPrice(doc: org.jsoup.nodes.Document): Double? {
        val priceText = doc.selectFirst("""div[class*="PriceBoard__priceInformation"] span[class*="StyledNumber__value"], span.PriceBoard__price""")?.text()?.replace(",", "")
        return priceText?.toDoubleOrNull()
    }

    private fun extractIncoming(doc: org.jsoup.nodes.Document): Double? {
        val dividendElement = doc.select("dt").find { it.text().contains("1株配当") }
        val valueText = dividendElement?.nextElementSibling()?.selectFirst("""span[class*="DataListItem__value"]""")?.text()
        return valueText?.toDoubleOrNull()
    }

    private fun extractEarningsDate(doc: org.jsoup.nodes.Document): LocalDate? {
        val earningsText = doc.select("p:contains(決算発表日)").first()?.text()
        if (earningsText == null || earningsText.contains("未定")) {
            return null
        }
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
