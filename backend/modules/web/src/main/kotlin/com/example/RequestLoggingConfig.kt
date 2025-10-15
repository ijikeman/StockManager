package com.example

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

/**
 * Registers a request logging filter that logs HTTP method, URI and payload for incoming requests.
 */
@Configuration
class RequestLoggingConfig {

    @Bean
    fun requestLoggingFilter(): CommonsRequestLoggingFilter {
        val loggingFilter = CommonsRequestLoggingFilter()
        loggingFilter.setIncludeClientInfo(true)
        loggingFilter.setIncludeQueryString(true)
        loggingFilter.setIncludePayload(true)
        // limit payload length to avoid huge logs
        loggingFilter.setMaxPayloadLength(1024)
        loggingFilter.setAfterMessagePrefix("REQUEST DATA : ")
        return loggingFilter
    }
}
