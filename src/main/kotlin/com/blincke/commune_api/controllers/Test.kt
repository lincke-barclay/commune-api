package com.blincke.commune_api.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("test")
class Test {
    @GetMapping("/auth")
    fun auth(principal: Principal): String {
        return principal.name
    }
}