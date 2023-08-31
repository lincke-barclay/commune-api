package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, String> {
    fun findFirstByEmail(email: String): User?
}