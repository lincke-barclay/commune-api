package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.domain.users.PublicUser

sealed interface CreateUserResult {
    data class Conflict(val conflictingUser: PublicUser) : CreateUserResult
    data class Created(val user: PublicUser) : CreateUserResult
    object UnknownFailure : CreateUserResult
}