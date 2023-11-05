package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.database.users.CommuneUser

data class CreateUserRequest(
        val firebaseId: String,
        val name: String,
        val email: String,
) {
    fun toCommuneUser() = CommuneUser(
            firebaseId = firebaseId,
            email = email,
            name = name,
            recipientToFriendRequests = setOf(),
            requestedFriends = setOf()
    )
}
