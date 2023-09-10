package com.blincke.commune_api.controllers.dto

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