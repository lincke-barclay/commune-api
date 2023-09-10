package com.blincke.commune_api.controllers.dto

import org.springframework.data.geo.Point

data class UserRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val locationLat: Double,
    val locationLon: Double,
)
