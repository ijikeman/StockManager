package com.example.stock.provider

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.File
import java.time.LocalDate

class YahooFinanceProviderTest {

    private lateinit var provider: YahooFinanceProvider
    private lateinit var connection: Connection
    private lateinit var disclosureConnection: Connection
    private lateinit var response: Connection.Response
    private lateinit var disclosureResponse: Connection.Response

    @BeforeEach
    fun initMocks() {
        val htmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance.html")
        val doc = Jsoup.parse(htmlFile, "UTF-8", "")
        val disclosureHtmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance-disclosure.html")
        val disclosureDoc = Jsoup.parse(disclosureHtmlFile, "UTF-8", "")

        connection = mock {
            on { execute() } doReturn response
        }
        disclosureConnection = mock {
            on { execute() } doReturn disclosureResponse
        }
        response = mock {
            on { statusCode() } doReturn 200
            on { parse() } doReturn doc
        }
        disclosureResponse = mock {
            on { statusCode() } doReturn 200
            on { parse() } doReturn disclosureDoc
        }

        provider = object : YahooFinanceProvider(0) {
            override fun connect(url: String): Connection {
                return if (url.endsWith("/disclosure")) disclosureConnection else connection
            }

            override fun currentDate(): LocalDate = LocalDate.of(2026, 1, 16)
        }
    }

    @Test
    fun `fetchLatestDisclosure should return correct date when disclosure date is before or after today`() {
        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(LocalDate.of(2025, 11, 12), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchStockInfo should return correct stock info`() {
        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(1234.5, stockInfo?.price)
        assertEquals(50.0, stockInfo?.incoming)
        assertEquals(LocalDate.of(2025, 10, 31), stockInfo?.earningsDate)
        assertEquals(LocalDate.of(2025, 11, 12), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchStockName should return correct name`() {
        val name = provider.fetchStockName("dummy")
        assertEquals("テスト株式会社", name)
    }

    @Test
    fun `toDoubleOrNull should return null when text is ---`() {
        val text = "---"
        val result = text.toDoubleOrNull()
        assertNull(result, "The text '---' should convert to null")
    }

    @Test
    fun `fetchLatestDisclosure should return previous year date when disclosure date is after today`() {
        // The dummy disclosure file has "11/12" and today is mocked to 2026/01/16
        val stockInfo = provider.fetchStockInfo("dummy")
        // The year should be 2025, not 2026
        assertEquals(LocalDate.of(2025, 11, 12), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchLatestDisclosure should return current year date when disclosure date is before today`() {
        // Create new mock document with a date before 2026/01/16
        val pastDateHtml = """
            <!DOCTYPE html><html><body>
            <div class="disclosureList_list"><div class="disclosureList_item">
            <ul class="DisclosureItem__supplements__1NHJ"><li class="DisclosureItem__supplement__2U1S"><time>1/15</time></li></ul>
            </div></div>
            </body></html>
        """
        val pastDateDoc = Jsoup.parse(pastDateHtml)
        val tempResponse = mock(Connection.Response::class.java)
        `when`(tempResponse.statusCode()).thenReturn(200)
        `when`(tempResponse.parse()).thenReturn(pastDateDoc)
        `when`(disclosureConnection.execute()).thenReturn(tempResponse)

        val stockInfo = provider.fetchStockInfo("dummy")
        // The year should be 2026
        assertEquals(LocalDate.of(2026, 1, 15), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchLatestDisclosure should handle time-based disclosure format`() {
        // A time-based format like "16:31" should use today's MM/DD
        val timeHtml = """
            <!DOCTYPE html><html><body>
            <div class="disclosureList_list"><div class="disclosureList_item">
            <ul class="DisclosureItem__supplements__1NHJ"><li class="DisclosureItem__supplement__2U1S"><time>16:31</time></li></ul>
            </div></div>
            </body></html>
        """
        val timeDoc = Jsoup.parse(timeHtml)
        val tempResponse = mock(Connection.Response::class.java)
        `when`(tempResponse.statusCode()).thenReturn(200)
        `when`(tempResponse.parse()).thenReturn(timeDoc)
        `when`(disclosureConnection.execute()).thenReturn(tempResponse)

        val stockInfo = provider.fetchStockInfo("dummy")
        // Today is 2026/01/16, so the parsed date should be 2026/01/16
        assertEquals(LocalDate.of(2026, 1, 16), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchLatestDisclosure should handle other Japanese date formats`() {
        // Pattern 1: YYYY年MM月DD日
        val datePattern1Html = """
            <!DOCTYPE html><html><body>
            <div class="disclosureList_list"><div class="disclosureList_item">
            <time>2025年10月28日</time>
            </div></div>
            </body></html>
        """
        val doc1 = Jsoup.parse(datePattern1Html)
        val tempResponse1 = mock(Connection.Response::class.java)
        `when`(tempResponse1.statusCode()).thenReturn(200)
        `when`(tempResponse1.parse()).thenReturn(doc1)
        `when`(disclosureConnection.execute()).thenReturn(tempResponse1)

        var stockInfo = provider.fetchStockInfo("dummy")
        assertEquals(LocalDate.of(2025, 10, 28), stockInfo?.latestDisclosureDate)

        // Pattern 2: YYYY/MM/DD
        val datePattern2Html = """
            <!DOCTYPE html><html><body>
            <div class="disclosureList_list"><div class="disclosureList_item">
            <time>2024/05/15</time>
            </div></div>
            </body></html>
        """
        val doc2 = Jsoup.parse(datePattern2Html)
        val tempResponse2 = mock(Connection.Response::class.java)
        `when`(tempResponse2.statusCode()).thenReturn(200)
        `when`(tempResponse2.parse()).thenReturn(doc2)
        `when`(disclosureConnection.execute()).thenReturn(tempResponse2)

        stockInfo = provider.fetchStockInfo("dummy")
        assertEquals(LocalDate.of(2024, 5, 15), stockInfo?.latestDisclosureDate)

        // Pattern 3: MM月DD日
        val datePattern3Html = """
            <!DOCTYPE html><html><body>
            <div class="disclosureList_list"><div class="disclosureList_item">
            <time>11月10日</time>
            </div></div>
            </body></html>
        """
        val doc3 = Jsoup.parse(datePattern3Html)
        val tempResponse3 = mock(Connection.Response::class.java)
        `when`(tempResponse3.statusCode()).thenReturn(200)
        `when`(tempResponse3.parse()).thenReturn(doc3)
        `when`(disclosureConnection.execute()).thenReturn(tempResponse3)

        stockInfo = provider.fetchStockInfo("dummy")
        // Year defaults to current year (2026)
        assertEquals(LocalDate.of(2026, 11, 10), stockInfo?.latestDisclosureDate)
    }

    @Test
    fun `fetchStockInfo should return null when connection fails completely`() {
        `when`(connection.execute()).thenThrow(RuntimeException("Network error"))
        val stockInfo = provider.fetchStockInfo("dummy")
        assertNull(stockInfo)
    }

    @Test
    fun `fetchStockInfo should retry on transient HTTP 500 error and succeed`() {
        val badResponse = mock(Connection.Response::class.java)
        `when`(badResponse.statusCode()).thenReturn(500)

        // Return HTTP 500 first, then HTTP 200
        `when`(connection.execute())
            .thenReturn(badResponse)
            .thenReturn(response)

        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(1234.5, stockInfo?.price)
    }

    @Test
    fun `fetchStockInfo should correctly extract previousPrice from script tag`() {
        val scriptHtml = """
            <!DOCTYPE html><html><head>
            <title>テスト株式会社【DUMMY】：ダミー情報 - Yahoo!ファイナンス</title>
            <script>
            window.__PRELOADED_STATE__ = {
                "context": {
                    "dispatcher": {
                        "stores": {
                            "QuoteStore": {
                                "previousPrice":"1200.5"
                            }
                        }
                    }
                }
            };
            </script>
            </head><body>
            <span class="PriceBoard__price">1,234.5</span>
            </body></html>
        """
        val scriptDoc = Jsoup.parse(scriptHtml)
        val tempResponse = mock(Connection.Response::class.java)
        `when`(tempResponse.statusCode()).thenReturn(200)
        `when`(tempResponse.parse()).thenReturn(scriptDoc)
        `when`(connection.execute()).thenReturn(tempResponse)

        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(1200.5, stockInfo?.previousPrice)
    }

    @Test
    fun `fetchStockInfo should handle missing or empty dividend gracefully`() {
        val noDivHtmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance-with-no-dividend.html")
        val noDivDoc = Jsoup.parse(noDivHtmlFile, "UTF-8", "")

        val tempResponse = mock(Connection.Response::class.java)
        `when`(tempResponse.statusCode()).thenReturn(200)
        `when`(tempResponse.parse()).thenReturn(noDivDoc)
        `when`(connection.execute()).thenReturn(tempResponse)

        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertNull(stockInfo?.incoming, "Dividend/incoming should be null when it is represented as '---'")
    }
}
