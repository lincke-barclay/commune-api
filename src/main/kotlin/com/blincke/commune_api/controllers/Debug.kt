package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.*
import com.blincke.commune_api.repositories.LocationRepository
import com.blincke.commune_api.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.Exception
import java.security.Principal

@RestController
@RequestMapping("test")
class Debug(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/auth")
    fun auth(principal: Principal): String {
        return principal.name
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<CommuneUserResponseDto>> {
        return try {
            ResponseEntity.ok(userRepository.findAll().map {
                toCommuneUserResponseDto(it)
            })
        } catch (e: Exception) {
            log.warn("Could not get new user with error", e)
            return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/locations")
    fun getLocations(): ResponseEntity<List<LocationResponseDto>> {
        return try {
            ResponseEntity.ok(locationRepository.findAll().map {
                toLocationResponseDto(it)
            })
        } catch (e: Exception) {
            log.warn("Could not get new user with error", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}