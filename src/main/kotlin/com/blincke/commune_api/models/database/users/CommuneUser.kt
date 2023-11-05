package com.blincke.commune_api.models.database.users

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.domain.users.PrivateUser
import com.blincke.commune_api.models.domain.users.PublicUser
import jakarta.persistence.*

@Entity
@Table(name = "commune_user")
class CommuneUser(
        @Id
        @Column(name = "firebase_id")
        val firebaseId: String,

        @Column(name = "name")
        val name: String,

        @Column(name = "email")
        val email: String,

        @OneToMany(mappedBy = "friendshipId.requester")
        val requestedFriends: Set<Friendship>,

        @OneToMany(mappedBy = "friendshipId.recipient")
        val recipientToFriendRequests: Set<Friendship>
) {
    fun toPublicUser() = PublicUser(
            name = name,
            id = firebaseId,
    )

    fun toPrivateUser(
    ) = PrivateUser(
            id = firebaseId,
            name = name,
            email = email,
    )

    fun copy(
            firebaseId: String? = null,
            name: String? = null,
            email: String? = null,
            requestedFriends: Set<Friendship>? = null,
            recipientToFriendRequests: Set<Friendship>? = null,
    ) = CommuneUser(
            firebaseId = firebaseId ?: this.firebaseId,
            name = name ?: this.name,
            email = email ?: this.email,
            requestedFriends = requestedFriends ?: this.requestedFriends,
            recipientToFriendRequests = recipientToFriendRequests ?: this.recipientToFriendRequests,
    )
}