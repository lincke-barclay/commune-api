package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.CommuneUser
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<CommuneUser, String> {
    fun findFirstByEmail(userEmail: String): CommuneUser?
}
