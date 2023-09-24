package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.CommuneUser
import java.time.Instant

data class CommuneUserResponseDto(
    val id: String,
    val createdTs: Instant,
    val lastUpdatedTs: Instant,
    val email: String,
    val firstName: String,
    val lastName: String,
    val home: LocationResponseDto,
)

fun toCommuneUserResponseDto(communeUser: CommuneUser): CommuneUserResponseDto {
    return with(communeUser) {
        CommuneUserResponseDto(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            email = email,
            firstName = firstName,
            lastName = lastName,
            home = toLocationResponseDto(home),
        )
    }
}