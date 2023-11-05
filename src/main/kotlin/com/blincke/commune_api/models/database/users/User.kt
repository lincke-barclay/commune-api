package com.blincke.commune_api.models.database.users

import com.blincke.commune_api.models.database.friends.Friendship
import jakarta.persistence.*

@Entity
@Table(name = "commune_user")
class User(
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
    fun copy(
            firebaseId: String? = null,
            name: String? = null,
            email: String? = null,
            requestedFriends: Set<Friendship>? = null,
            recipientToFriendRequests: Set<Friendship>? = null,
    ) = User(
            firebaseId = firebaseId ?: this.firebaseId,
            name = name ?: this.name,
            email = email ?: this.email,
            requestedFriends = requestedFriends ?: this.requestedFriends,
            recipientToFriendRequests = recipientToFriendRequests ?: this.recipientToFriendRequests,
    )
}