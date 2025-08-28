package com.example.stock

import com.example.stock.model.Owner
import com.example.stock.service.OwnerService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
// BindingResultを使うことでValidationエラーを拾うことができ、Thymeleafテンプレートでエラーメッセージを${#field}を使って表示させることができる
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class OwnerController(
    private val ownerService: OwnerService
) {
    // view OwnerList
    @GetMapping("/owner")
    fun ownerList(model: Model): String {
        model.addAttribute("owner", ownerService.findAll())
        // add owner form
        model.addAttribute("ownerForm", Owner(name = ""))
        return "owner"
    }

    // add Owner
    @PostMapping("/owner")
    fun ownerRegister(
        @Validated @ModelAttribute("ownerForm") owner: Owner,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (bindingResult.hasErrors()) { // validateionエラーがあった場合
            model.addAttribute("owner", ownerService.findAll())
            return "owner"
        }
        ownerService.save(owner)
        return "redirect:/owner"
    }

    @GetMapping("/owner/edit/{id}")
    fun ownerEdit(
        @PathVariable id: Int, // URLの一部に引数のidを割り当てる為、@PathVariableを定義
        model: Model
    ): String {
        model.addAttribute("owner", ownerService.findById(id))
        return "owner_edit"
    }

    // update Owner
    @PostMapping("/owner/update")
    fun ownerUpdate(
        @Validated @ModelAttribute owner: Owner,
        bindingResult: BindingResult
    ): String {
        if (bindingResult.hasErrors()) { // validateionエラーがあった場合
            return "owner_edit"
        }
        ownerService.save(owner)
        return "redirect:/owner"
    }

    // delete Owner
    @PostMapping("/owner/delete/{id}")
    fun ownerDelete(
        @PathVariable id: Int
    ): String {
        ownerService.deleteById(id)
        return "redirect:/owner"
    }
}
