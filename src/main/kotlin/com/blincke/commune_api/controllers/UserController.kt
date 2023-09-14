package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.UserDto
import com.blincke.commune_api.controllers.dto.UserPostDto
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("users")
class UserController {
    private val log = LoggerFactory.getLogger(this.javaClass)

//    @PostMapping
//    fun createUser(@RequestBody newUser: UserPostDto): ResponseEntity<UserDto> {
//
//    }

    @GetMapping(path = ["/test"])
    fun getTest(): ResponseEntity<String> {
        return ResponseEntity.ok("HELLO WORLD")
    }
}