package com.blincke.commune_api.models.firebase

import com.blincke.commune_api.models.database.users.User

data class FirebaseUser(
        val uid: String,
        val name: String,
        val email: String,
) {

    /**
     * If the firebase user doesn't exist in the database,
     * this method is used to create a new user for the database
     * that has no friends
     */
    fun toNewCommuneUser() = User(
            firebaseId = uid,
            name = name,
            email = email,
            requestedFriends = setOf(),
            recipientToFriendRequests = setOf(),
    )

    /**
     * Checks if the firebase user is in sync with the
     * commune database user
     */
    fun isInSyncWithCommuneUser(user: User) =
            email == user.email && name == user.name

    /**
     * Returns a new commune database user that
     * is up-to-date with the firebase user
     */
    fun mergeWithCommuneUser(user: User) =
            user.copy(
                    email = email,
                    name = name,
            )
}
