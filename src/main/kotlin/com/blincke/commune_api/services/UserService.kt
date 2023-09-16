package com.blincke.commune_api.services

import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.models.Location
import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.repositories.LocationRepository
import com.blincke.commune_api.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val locationService: LocationService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    fun createUser(email: String, firstName: String, lastName: String, homePoint: Point): CommuneUser {
        val homeLocation = locationService.getOrCreateLocation(locationPoint = homePoint)
        return userRepository.save(
            CommuneUser(
                email = email,
                firstName = firstName,
                lastName = lastName,
                home = homeLocation,
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