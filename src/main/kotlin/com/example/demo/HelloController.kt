package com.example.demo

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.stereotype.Controller // @RestController から @Controller に変更

//@RestController
@Controller
class HelloController {

    @GetMapping("/")
    fun hello(): String {
        return "hello" // src/main/resources/templates/hello.htmlを返す
    }
}
