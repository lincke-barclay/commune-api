package com.blincke.commune_api.models.network.users.egress

import com.blincke.commune_api.models.database.users.User

data class PublicUserResponseDto(
        val id: String,
        val name: String,
)

fun User.toPublicUserResponseDto() =
        PublicUserResponseDto(
                id = firebaseId,
                name = name,
        )

