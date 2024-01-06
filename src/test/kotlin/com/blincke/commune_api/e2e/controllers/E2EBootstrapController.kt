package com.blincke.commune_api.com.blincke.commune_api.e2e.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Controller
@RestController
class E2EBootstrapController {
    @GetMapping("/bootstrap")
    fun bootstrap(): ResponseEntity<String> {
        return ResponseEntity.ok("foo")
    }
}
