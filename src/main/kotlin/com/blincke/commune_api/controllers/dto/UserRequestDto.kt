package com.blincke.commune_api.controllers.dto

data class UserRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val locationLat: Double,
    val locationLon: Double,
)
