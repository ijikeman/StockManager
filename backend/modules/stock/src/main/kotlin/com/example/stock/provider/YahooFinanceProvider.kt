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
            val previousClose = extractPreviousClose(doc) // 前日比株価

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

    // 株価を抽出する関数
    private fun extractPrice(doc: org.jsoup.nodes.Document): Double? {
        val priceText = doc.selectFirst("""div[class*="PriceBoard__priceInformation"] span[class*="StyledNumber__value"], span.PriceBoard__price""")?.text()?.replace(",", "")
        return priceText?.toDoubleOrNull()
    }

    // 配当金を抽出する関数
    private fun extractIncoming(doc: org.jsoup.nodes.Document): Double? {
        val dividendElement = doc.select("dt").find { it.text().contains("1株配当") }
        val valueText = dividendElement?.nextElementSibling()?.selectFirst("""span[class*="DataListItem__value"]""")?.text()
        return valueText?.toDoubleOrNull()
    }

    // 決算発表日を抽出する関数
    private fun extractEarningsDate(doc: org.jsoup.nodes.Document): LocalDate? {
        val earningsText = doc.select("p:contains(決算発表日)").first()?.text()
        if (earningsText == null || earningsText.contains("未定")) {
            return null
        }
        // YYYY年MM月上旬、中旬、下旬の形式に対応
        val seasonPattern = Pattern.compile("(\\d{4})年(\\d{1,2})月(上旬|中旬|下旬)")
        val seasonMatcher = seasonPattern.matcher(earningsText)

        if (seasonMatcher.find()) {
            val year = seasonMatcher.group(1).toInt()
            val month = seasonMatcher.group(2).toInt()
            val season = seasonMatcher.group(3)

            val day = when (season) {
                "上旬" -> 10
                "中旬" -> 15
                "下旬" -> 25
                else -> 1
            }
            return LocalDate.of(year, month, day)
        }
        // YYYY年MM月DD日の形式に対応
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

    /* 前日比[PriceChangeLabel__primary](表示されていない場合は---が表示される)と
       "StyledNumber__value__3rXW"は変わる可能性がある
    例: input HTML
<dl class="PriceChangeLabel__definition__3Jdj">
    <dt class="PriceChangeLabel__term__3H4k">前日比</dt>
    <dd class="PriceChangeLabel__description__a5Lp">
        <span class="StyledNumber__1fof StyledNumber--horizontal__HwH8 PriceChangeLabel__prices__30Ey">
            <span class="StyledNumber__item__1-yu StyledNumber__item--empty__X07R PriceChangeLabel__primary__Y_ut">
                <span class="StyledNumber__value__3rXW">-300</span>
            </span>
            <span class="StyledNumber__item__1-yu StyledNumber__item--secondary__RTJc StyledNumber__item--small__2hJE PriceChangeLabel__secondary__3BXI">
                <span class="StyledNumber__punctuation__3pWV">(</span>
                <span class="StyledNumber__value__3rXW">0.00</span>
                <span class="StyledNumber__suffix__2SD5">%</span>
                <span class="StyledNumber__punctuation__3pWV">)</span>
            </span>
        </span>
    </dd>
</dl>
    */
    private fun extractPreviousClose(doc: org.jsoup.nodes.Document): Double? {
        val previousCloseElement = doc.select("dt").find { it.text().contains("前日比") }

        // PriceChangeLabel__primaryの値を取得
        val previousCloseText = previousCloseElement?.nextElementSibling()?.selectFirst("""span[class*="PriceChangeLabel__primary"] span[class*="StyledNumber__value"]""")?.text()

        // 数値が取得できない場合はnullを返す
        if (previousCloseText == null) {
            return null
        } else {
            return try {
                previousCloseText.replace(",", "").toDouble()
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}
