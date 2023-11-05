package com.blincke.commune_api.models.firebase

import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.users.egress.CreateUserRequest

data class FirebaseUser(
        val uid: String,
        val name: String,
        val email: String,
) {
    fun toCreateUserRequest() = CreateUserRequest(
            firebaseId = uid,
            name = name,
            email = email,
    )

    fun isInSyncWithCommuneUser(communeUser: CommuneUser) =
            email == communeUser.email && name == communeUser.name

    fun mergeWithCommuneUser(communeUser: CommuneUser) =
            communeUser.copy(
                    email = email,
                    name = name,
            )
}
