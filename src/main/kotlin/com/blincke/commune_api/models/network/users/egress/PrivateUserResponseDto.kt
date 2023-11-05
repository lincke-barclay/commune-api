package com.blincke.commune_api.models.network.users.egress

import com.blincke.commune_api.models.domain.users.PrivateUser
import kotlinx.serialization.Serializable

@Serializable
data class PrivateUserResponseDto(
        val id: String,
        val name: String,
        val email: String,
)

fun PrivateUser.toPrivateUserResponseDto() =
        PrivateUserResponseDto(
                id = id,
                name = name,
                email = email,
        )

