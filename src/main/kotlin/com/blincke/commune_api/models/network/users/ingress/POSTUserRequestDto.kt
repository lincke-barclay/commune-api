package com.blincke.commune_api.models.network.users.ingress

import com.blincke.commune_api.models.database.users.CommuneUser

data class POSTUserRequestDto(
        val email: String,
        val firstName: String,
        val lastName: String,
) {
    fun toNewCommuneUser() = CommuneUser(
            firstName = firstName,
            lastName = lastName,
            email = email,
            requestedFriends = emptySet(),
            recipientToFriendRequests = emptySet(),
    )
}
