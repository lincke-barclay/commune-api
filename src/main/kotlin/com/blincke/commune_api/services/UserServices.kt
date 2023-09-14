package com.blincke.commune_api.services

import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServices(
    private val userRepository: UserRepository
) {

    fun getUserById(userId: String): CommuneUser? {
        return userRepository.findById(userId).get()
    }

    fun getUserByEmail(email: String): CommuneUser? {
        return userRepository.findFirstByEmail(email)
    }
}