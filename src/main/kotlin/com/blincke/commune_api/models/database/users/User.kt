package com.blincke.commune_api.models.database.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

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

    @Column(name = "profile_picture_url")
    val profilePictureUrl: String? = null,

    @OneToMany(mappedBy = "friendshipId.requester", cascade = [CascadeType.ALL], orphanRemoval = true)
    val friendRequestsISent: MutableSet<Friendship>,

    @OneToMany(mappedBy = "friendshipId.recipient", cascade = [CascadeType.ALL], orphanRemoval = true)
    val friendRequestsSentToMe: MutableSet<Friendship>,
) {
    fun copy(
        firebaseId: String? = null,
        name: String? = null,
        email: String? = null,
        profilePictureUrl: String? = null,
    ) = User(
        firebaseId = firebaseId ?: this.firebaseId,
        name = name ?: this.name,
        email = email ?: this.email,
        profilePictureUrl = profilePictureUrl ?: this.profilePictureUrl,
    )
}
