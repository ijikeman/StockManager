package com.example.stock.provider

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import java.io.File
import java.time.LocalDate

class YahooFinanceProviderTest {

    private lateinit var provider: YahooFinanceProvider
    private lateinit var doc: org.jsoup.nodes.Document
    private lateinit var mockedJsoup: MockedStatic<Jsoup>

    @BeforeEach
    fun setUp() {
        provider = YahooFinanceProvider(0) // requestDelayMillis = 0
        val htmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance.html")
        doc = Jsoup.parse(htmlFile, "UTF-8", "")

        // Mock Jsoup.connect to avoid actual network calls
        val connection = mock(Connection::class.java)
        `when`(connection.get()).thenReturn(doc)
        mockedJsoup = mockStatic(Jsoup::class.java)
        mockedJsoup.`when`<Connection> { Jsoup.connect(anyString()) }.thenReturn(connection)
    }

    @AfterEach
    fun tearDown() {
        mockedJsoup.close()
    }

    @Test
    fun `fetchStockInfo should return correct stock info`() {
        val stockInfo = provider.fetchStockInfo("dummy")
        assertNotNull(stockInfo)
        assertEquals(1234.5, stockInfo?.price)
        assertEquals(50.0, stockInfo?.incoming)
        assertEquals(LocalDate.of(2025, 10, 31), stockInfo?.earnings_date)
    }

    @Test
    fun `fetchStockName should return correct name`() {
        val name = provider.fetchStockName("dummy")
        assertEquals("テスト株式会社", name)
    }
}
