package com.example.stock

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.stereotype.Controller

@Controller
class HelloController {

    @GetMapping("/")
    fun hello(): String {
        return "hello" // src/main/resources/templates/hello.htmlを返す
    }
}
