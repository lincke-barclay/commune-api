package com.blincke.commune_api.services

import com.blincke.commune_api.models.User
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.stereotype.Service

@Service
class UserServices(
    private val userRepository: UserRepository
) {

    fun getUserById(userId: String): User? {
        return userRepository.findById(userId).get() ?: null
    }

    fun getUserByEmail(email: String): User? {
        return userRepository.findFirstByEmail(email)
    }
}