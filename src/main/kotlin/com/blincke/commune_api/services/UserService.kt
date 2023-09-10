package com.blincke.commune_api.services

import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.models.Location
import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun createUser(email: String, firstName: String, lastName: String, home: Point): CommuneUser {
        return userRepository.save(
            CommuneUser(
                email = email,
                firstName = firstName,
                lastName = lastName,
                home = Location(point = home),
            )
        )
    }

    fun getUserByEmail(userEmail: String): CommuneUser {
        return userRepository.findFirstByEmail(userEmail) ?: run {
            log.info("No user found for email $userEmail")
            throw UserNotFoundException()
        }
    }
}