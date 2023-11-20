package com.blincke.commune_api.models.database.friends

import com.blincke.commune_api.models.database.users.User
import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Embeddable
data class FriendshipId(
        @ManyToOne
        @JoinColumn(name = "requester", referencedColumnName = "firebase_id")
        val requester: User,
        @ManyToOne
        @JoinColumn(name = "recipient", referencedColumnName = "firebase_id")
        val recipient: User,
)
