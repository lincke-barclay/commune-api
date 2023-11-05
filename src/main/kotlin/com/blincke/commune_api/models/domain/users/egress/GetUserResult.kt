package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.database.users.User

sealed interface GetUserResult {
    data object DoesntExist : GetUserResult
    data class Active(val user: User) : GetUserResult
}
