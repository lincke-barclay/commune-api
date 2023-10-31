package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.database.users.CommuneUser

sealed interface GetCommuneUserResult {
    object DoesntExist : GetCommuneUserResult
    data class Active(val user: CommuneUser) : GetCommuneUserResult
}
