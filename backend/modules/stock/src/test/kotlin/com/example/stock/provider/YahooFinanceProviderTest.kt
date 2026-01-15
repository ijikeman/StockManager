package com.example.stock.provider

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import java.io.File
import java.time.LocalDate

class YahooFinanceProviderTest {

    private lateinit var provider: YahooFinanceProvider
    private lateinit var doc: Document
    private lateinit var disclosureDoc: Document
    private lateinit var mockedJsoup: MockedStatic<Jsoup>
    private lateinit var mockedLocalDate: MockedStatic<LocalDate>
    private val connection: Connection = mock(Connection::class.java)
    private val disclosureConnection: Connection = mock(Connection::class.java)

    @BeforeEach
    fun setUp() {
        provider = YahooFinanceProvider(0) // requestDelayMillis = 0
        val htmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance.html")
        doc = Jsoup.parse(htmlFile, "UTF-8", "")
        val disclosureHtmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance-disclosure.html")
        disclosureDoc = Jsoup.parse(disclosureHtmlFile, "UTF-8", "")


        // Mock Jsoup.connect to avoid actual network calls
        `when`(connection.get()).thenReturn(doc)
        `when`(disclosureConnection.get()).thenReturn(disclosureDoc)
        mockedJsoup = mockStatic(Jsoup::class.java)
        mockedJsoup.`when`<Connection> { Jsoup.connect(argThat { it.endsWith("/disclosure") }) }.thenReturn(disclosureConnection)
        mockedJsoup.`when`<Connection> { Jsoup.connect(argThat { !it.endsWith("/disclosure") }) }.thenReturn(connection)


        // Mock LocalDate.now()
        val fixedDate = LocalDate.of(2026, 1, 16)
        mockedLocalDate = mockStatic(LocalDate::class.java, CALLS_REAL_METHODS)
        `when`(LocalDate.now()).thenReturn(fixedDate)
    }

    @AfterEach
    fun tearDown() {
        mockedJsoup.close()
        mockedLocalDate.close()
    }

    @Test
    fun `fetchStockInfo should return correct stock info`() {
        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(1234.5, stockInfo?.price)
        assertEquals(50.0, stockInfo?.incoming)
        assertEquals(LocalDate.of(2025, 10, 31), stockInfo?.earningsDate)
        // Note: latestDisclosureDate is now tested in dedicated tests
    }

    @Test
    fun `fetchStockName should return correct name`() {
        val name = provider.fetchStockName("dummy")
        assertEquals("テスト株式会社", name)
    }

    @Test
    fun `toDoubleOrNull should return null when text is ---`() {
        // Test that the "---" string correctly converts to null
        val text = "---"
        val result = text.toDoubleOrNull()
        
        // Verify that "---" is correctly parsed as null
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
        `when`(disclosureConnection.get()).thenReturn(pastDateDoc)

        val stockInfo = provider.fetchStockInfo("dummy")
        // The year should be 2026
        assertEquals(LocalDate.of(2026, 1, 15), stockInfo?.latestDisclosureDate)
    }
}
