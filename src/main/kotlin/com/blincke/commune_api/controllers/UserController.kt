package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.CommuneUserRequestDto
import com.blincke.commune_api.controllers.dto.toCommuneUserResponseDto
import com.blincke.commune_api.controllers.dto.CommuneUserResponseDto
import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.services.CommuneUserService
import org.slf4j.LoggerFactory
import org.springframework.data.geo.Point
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
    private val communeUserService: CommuneUserService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createUser(
        @RequestBody communeUserRequestDto: CommuneUserRequestDto,
    ): ResponseEntity<CommuneUserResponseDto> {
        return try {
            ResponseEntity.ok(
                toCommuneUserResponseDto(
                    with(communeUserRequestDto) {
                        communeUserService.createUser(
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            homePoint = Point(locationLat, locationLon),
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
    ): ResponseEntity<CommuneUserResponseDto> {
        return try {
            ResponseEntity.ok(
                toCommuneUserResponseDto(communeUserService.getUserByEmail(userEmail = userEmail))
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
