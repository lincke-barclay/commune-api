package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.domain.users.PrivateUser

sealed interface GetPrivateUserResult {
    object DoesntExist : GetPrivateUserResult
    data class Active(val user: PrivateUser) : GetPrivateUserResult
}
