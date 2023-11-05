package com.blincke.commune_api.models.network.users.egress

import com.blincke.commune_api.models.database.users.User
import kotlinx.serialization.Serializable

@Serializable
data class PrivateUserResponseDto(
        val id: String,
        val name: String,
        val email: String,
)

fun User.toPrivateUserResponseDto() =
        PrivateUserResponseDto(
                id = firebaseId,
                name = name,
                email = email,
        )

