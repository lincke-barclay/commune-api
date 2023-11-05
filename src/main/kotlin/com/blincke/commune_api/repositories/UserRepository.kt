package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.users.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String>
