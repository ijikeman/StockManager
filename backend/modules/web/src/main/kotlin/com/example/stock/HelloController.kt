package com.example.stock

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/api")
class HelloController {
    @GetMapping("/hello") // /api/helloへのリクエストを処理
    fun hello(): String = "Hello, World!(Spring Boot)"
}
