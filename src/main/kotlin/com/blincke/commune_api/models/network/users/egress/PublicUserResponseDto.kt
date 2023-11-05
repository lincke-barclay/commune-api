package com.blincke.commune_api.models.network.users.egress

import com.blincke.commune_api.models.domain.users.PublicUser

data class PublicUserResponseDto(
        val id: String,
        val name: String,
)

fun PublicUser.toPublicUserResponseDto() =
        PublicUserResponseDto(
                id = id,
                name = name,
        )

