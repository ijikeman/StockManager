package com.example.stock.provider

import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.jsoup.Jsoup
import java.io.File
import java.time.LocalDate

class YahooFinanceProviderTest {

    private lateinit var provider: YahooFinanceProvider
    private lateinit var doc: org.jsoup.nodes.Document

    @BeforeEach
    fun setUp() {
        provider = YahooFinanceProvider(0) // requestDelayMillis = 0
        val htmlFile = File("src/test/resources/com/example/stock/provider/dummy-yahoo-finance.html")
        doc = Jsoup.parse(htmlFile, "UTF-8")
    }

    private fun getJsonNode(): JsonNode {
        val method = provider::class.java.getDeclaredMethod("getPreloadedState", org.jsoup.nodes.Document::class.java)
        method.isAccessible = true
        return method.invoke(provider, doc) as JsonNode
    }

    @Test
    fun `getPreloadedState should return json`() {
        val jsonNode = getJsonNode()
        assertNotNull(jsonNode)
    }

    @Test
    fun `extractName should return correct name`() {
        val jsonNode = getJsonNode()
        val method = provider::class.java.getDeclaredMethod("extractName", JsonNode::class.java)
        method.isAccessible = true
        val name = method.invoke(provider, jsonNode)
        assertEquals("テスト株式会社", name)
    }

    @Test
    fun `extractPrice should return correct price`() {
        val jsonNode = getJsonNode()
        val method = provider::class.java.getDeclaredMethod("extractPrice", JsonNode::class.java)
        method.isAccessible = true
        val price = method.invoke(provider, jsonNode)
        assertEquals(1234.5, price)
    }

    @Test
    fun `extractDividend should return correct dividend`() {
        val jsonNode = getJsonNode()
        val method = provider::class.java.getDeclaredMethod("extractDividend", JsonNode::class.java)
        method.isAccessible = true
        val dividend = method.invoke(provider, jsonNode)
        assertEquals(50.0, dividend)
    }

    @Test
    fun `extractEarningsDate should return correct date`() {
        val jsonNode = getJsonNode()
        val method = provider::class.java.getDeclaredMethod("extractEarningsDate", JsonNode::class.java)
        method.isAccessible = true
        val date = method.invoke(provider, jsonNode)
        assertEquals(LocalDate.of(2025, 10, 31), date)
    }
}
