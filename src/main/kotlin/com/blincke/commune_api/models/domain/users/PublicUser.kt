package com.blincke.commune_api.models.domain.users

/**
 * A Simplified user that does not contain sensitive information
 * It is e.g. "Public" in that any user who is authenticated (but
 * not authorized) can get it
 */
data class PublicUser(
        val name: String,
        val id: String,
)
