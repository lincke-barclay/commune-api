package com.blincke.commune_api.models.domain.users

import java.time.Instant

data class PrivateUser(
        val id: String,
        val createdTs: Instant,
        val lastUpdatedTs: Instant,
        val email: String,
        val firstName: String,
        val lastName: String,
)
