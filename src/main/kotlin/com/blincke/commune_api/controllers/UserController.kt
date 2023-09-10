package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.UserResponseDto
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UserController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createUser(): ResponseEntity<UserResponseDto> {

    }

    @GetMapping
    fun getUserByEmail(
        @RequestParam userEmail: String,
    ): ResponseEntity<UserResponseDto> {}
}