package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.User
import org.joda.time.DateTime

data class UserResponseDto(
    val id: String,
    val createdTs: DateTime,
    val lastUpdatedTs: DateTime,
    val email: String,
    val firstName: String,
    val lastName: String,
    val home: LocationResponseDto,
)

fun UserResponse(user: User): UserResponseDto {
    return with(user) {
        UserResponseDto(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            email = email,
            firstName = firstName,
            lastName = lastName,
            home = LocationResponse(home),
        )
    }
}