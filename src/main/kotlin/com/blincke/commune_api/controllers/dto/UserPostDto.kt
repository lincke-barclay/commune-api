package com.blincke.commune_api.controllers.dto

data class UserPostDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val homeLat: Double,
    val homeLong: Double,
)
