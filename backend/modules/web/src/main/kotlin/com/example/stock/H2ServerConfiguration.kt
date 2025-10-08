package com.example.stock

import org.h2.tools.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import java.sql.SQLException

@Configuration
class H2ServerConfiguration {

    private var webServer: Server? = null

    @EventListener(ContextRefreshedEvent::class)
    @Throws(SQLException::class)
    fun start() {
        webServer = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092").start()
    }

    @EventListener(ContextClosedEvent::class)
    fun stop() {
        webServer?.stop()
    }
}