package com.blincke.commune_api.models.network.users.egress

import com.blincke.commune_api.models.domain.users.PublicUser
import java.time.Instant

data class PublicUserResponseDto(
        val id: String,
        val firstName: String,
        val lastName: String,
)

fun PublicUser.toUserResponseDto() =
        PublicUserResponseDto(
                id = id,
                firstName = firstName,
                lastName = lastName,
        )
