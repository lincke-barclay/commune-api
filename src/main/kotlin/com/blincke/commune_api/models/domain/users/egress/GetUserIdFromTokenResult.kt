package com.blincke.commune_api.models.domain.users.egress

sealed interface GetUserIdFromTokenResult {
    data class Authenticated(val id: String) : GetUserIdFromTokenResult
    object Unauthenticated : GetUserIdFromTokenResult
}
