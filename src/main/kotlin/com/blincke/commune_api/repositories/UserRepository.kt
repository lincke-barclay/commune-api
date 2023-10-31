package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.users.CommuneUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository: JpaRepository<CommuneUser, String> {
    fun findFirstByEmail(userEmail: String): CommuneUser?
}
