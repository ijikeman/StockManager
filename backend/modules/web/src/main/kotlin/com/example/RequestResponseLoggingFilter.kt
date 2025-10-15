package com.example

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.Charset

@Component
class RequestResponseLoggingFilter : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(RequestResponseLoggingFilter::class.java)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val wrappedRequest = if (request is ContentCachingRequestWrapper) request else ContentCachingRequestWrapper(request)
        val wrappedResponse = if (response is ContentCachingResponseWrapper) response else ContentCachingResponseWrapper(response)

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            logRequest(wrappedRequest)
            logResponse(wrappedResponse)
            // copy response body back to the original response
            wrappedResponse.copyBodyToResponse()
        }
    }

    private fun logRequest(request: ContentCachingRequestWrapper) {
        try {
            val method = request.method
            val uri = request.requestURI + (if (request.queryString != null) "?" + request.queryString else "")
            val charset = try {
                Charset.forName(request.characterEncoding ?: "UTF-8")
            } catch (e: Exception) {
                Charset.forName("UTF-8")
            }
            val payload = request.contentAsByteArray.takeIf { it.isNotEmpty() }?.let { String(it, charset) } ?: ""
            logger.info("REQUEST -> method={}, uri={}, client={}, payload={}", method, uri, request.remoteAddr, payload)
        } catch (e: Exception) {
            logger.warn("Failed to log request payload", e)
        }
    }

    private fun logResponse(response: ContentCachingResponseWrapper) {
        try {
            val status = response.status
            val respCharset = try {
                Charset.forName(response.characterEncoding ?: "UTF-8")
            } catch (e: Exception) {
                Charset.forName("UTF-8")
            }
            val payload = response.contentAsByteArray.takeIf { it.isNotEmpty() }?.let { String(it, respCharset) } ?: ""
            logger.info("RESPONSE -> status={}, payload={}", status, payload)
        } catch (e: Exception) {
            logger.warn("Failed to log response payload", e)
        }
    }
}
