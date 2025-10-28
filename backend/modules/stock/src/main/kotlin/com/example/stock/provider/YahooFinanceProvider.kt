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
        private val PREVIOUS_PRICE_PATTERN = Pattern.compile(""""previousPrice":"([0-9,]+(?:\.[0-9]+)?)"""")
        private const val DISCLOSURE_PATH = "/disclosure"
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
            val previousPrice = extractPreviousPrice(doc)
            
            // 適時開示情報を取得
            val (latestDisclosureDate, latestDisclosureUrl) = fetchLatestDisclosure(code)

            // 取得したデータをStockInfoに格納しreturnする
            StockInfo(price, incoming, earnings_date, previousPrice, latestDisclosureDate, latestDisclosureUrl)
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

    private fun extractPreviousPrice(doc: org.jsoup.nodes.Document): Double? {
        return extractFromPreloadedState(doc, PREVIOUS_PRICE_PATTERN)
    }

    private fun extractFromPreloadedState(doc: org.jsoup.nodes.Document, pattern: Pattern): Double? {
        val scriptContent = doc.select("script").find { it.html().contains("__PRELOADED_STATE__") }?.html()
        if (scriptContent != null) {
            val matcher = pattern.matcher(scriptContent)
            if (matcher.find()) {
                val valueText = matcher.group(1).replace(",", "")
                return valueText.toDoubleOrNull()
            }
        }
        return null
    }

    /**
     * 適時開示ページから最新の開示情報を取得します。
     *
     * @param code 銘柄コード
     * @return Pair(開示日, 開示URL) or (null, null)
     */
    private fun fetchLatestDisclosure(code: String): Pair<LocalDate?, String?> {
        // リクエスト間の遅延を挿入します。
        Thread.sleep(requestDelayMillis)

        return try {
            val disclosureUrl = "$BASE_URL/$code.T$DISCLOSURE_PATH"
            val doc = Jsoup.connect(disclosureUrl).get()
            
            // 適時開示のリストから最初のアイテム（最新）を取得
            val firstDisclosureItem = doc.selectFirst("div.disclosureList_list > div.disclosureList_item")
                ?: doc.selectFirst("div[class*='disclosureList'] div[class*='item']")
                ?: doc.selectFirst("table.disclosure tbody tr")
            
            if (firstDisclosureItem != null) {
                // 日付を抽出
                val dateElement = firstDisclosureItem.selectFirst("time") 
                    ?: firstDisclosureItem.selectFirst("span[class*='date']")
                    ?: firstDisclosureItem.selectFirst("td:first-child")
                
                val dateText = dateElement?.text()
                val disclosureDate = parseDateFromDisclosure(dateText)
                
                // URLを抽出
                val linkElement = firstDisclosureItem.selectFirst("a")
                val detailUrl = linkElement?.attr("href")?.let { href ->
                    if (href.startsWith("http")) {
                        href
                    } else if (href.startsWith("/")) {
                        "https://finance.yahoo.co.jp$href"
                    } else {
                        null
                    }
                }
                
                Pair(disclosureDate, detailUrl)
            } else {
                Pair(null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }

    /**
     * 適時開示の日付テキストをLocalDateにパースします。
     * 
     * @param dateText 日付テキスト（例: "2025年10月28日", "2025/10/28", "10月28日"）
     * @return LocalDate or null
     */
    private fun parseDateFromDisclosure(dateText: String?): LocalDate? {
        if (dateText.isNullOrBlank()) {
            return null
        }
        
        return try {
            // パターン1: "2025年10月28日" 形式
            val pattern1 = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日")
            val matcher1 = pattern1.matcher(dateText)
            if (matcher1.find()) {
                val year = matcher1.group(1).toInt()
                val month = matcher1.group(2).toInt()
                val day = matcher1.group(3).toInt()
                return LocalDate.of(year, month, day)
            }
            
            // パターン2: "2025/10/28" 形式
            val pattern2 = Pattern.compile("(\\d{4})/(\\d{1,2})/(\\d{1,2})")
            val matcher2 = pattern2.matcher(dateText)
            if (matcher2.find()) {
                val year = matcher2.group(1).toInt()
                val month = matcher2.group(2).toInt()
                val day = matcher2.group(3).toInt()
                return LocalDate.of(year, month, day)
            }
            
            // パターン3: "10月28日" 形式（年は現在の年を使用）
            val pattern3 = Pattern.compile("(\\d{1,2})月(\\d{1,2})日")
            val matcher3 = pattern3.matcher(dateText)
            if (matcher3.find()) {
                val month = matcher3.group(1).toInt()
                val day = matcher3.group(2).toInt()
                val currentYear = LocalDate.now().year
                return LocalDate.of(currentYear, month, day)
            }
            
            null
        } catch (e: Exception) {
            null
        }
    }
}
