package com.blincke.commune_api.controllers.dto

import java.util.Date

data class UserDto(
    val id: String,
    val createdTs: Date,
    val lastUpdatedTs: Date,
    val email: String,
    val firstName: String,
    val lastName: String,
    val home: LocationDto,
)
