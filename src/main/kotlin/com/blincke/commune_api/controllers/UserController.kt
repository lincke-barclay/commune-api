package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.UserRequestDto
import com.blincke.commune_api.controllers.dto.UserResponse
import com.blincke.commune_api.controllers.dto.UserResponseDto
import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception

@RestController
@RequestMapping("users")
class UserController(
    private val userService: UserService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createUser(
        @RequestBody userRequestDto: UserRequestDto,
    ): ResponseEntity<UserResponseDto> {
        return try {
            ResponseEntity.ok(
                UserResponse(
                    with(userRequestDto) {
                        userService.createUser(
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            home = location,
                        )
                    }
                )
            )
        } catch (e: Exception) {
            log.warn("Could not create new user with error", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    fun getUserByEmail(
        @RequestParam userEmail: String,
    ): ResponseEntity<UserResponseDto> {
        return try {
            ResponseEntity.ok(
                UserResponse(userService.getUserByEmail(userEmail = userEmail))
            )
        } catch (e: Exception) {
            log.warn("Could not get new user with error", e)
            return when (e) {
                is UserNotFoundException -> ResponseEntity(HttpStatus.NOT_FOUND)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}