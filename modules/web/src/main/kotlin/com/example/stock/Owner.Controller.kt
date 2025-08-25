package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class OwnerController(
    private val ownerService: OwnerService
) {
    @GetMapping("/owner")
    fun ownerList(model: Model): String {
        model.addAttribute("owner", ownerService.findAll())
        return "owner"
    }

    @PostMapping("/owner")
    fun ownerRegister(@RequestParam name: String): String {
        ownerService.save(Owner(name = name))
        return "redirect:/owner"
    }
}
