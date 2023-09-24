package com.blincke.commune_api.controllers.dto

data class CommuneUserRequestDto(
    val email: String,
    val firstName: String,
    val lastName: String,
    val locationLat: Double,
    val locationLon: Double,
)
