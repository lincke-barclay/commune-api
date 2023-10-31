package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.domain.users.PublicUser

sealed interface GetPublicUserResult {
    object DoesntExist : GetPublicUserResult
    data class Active(val user: PublicUser) : GetPublicUserResult
}
