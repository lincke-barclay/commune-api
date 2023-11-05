package com.blincke.commune_api.models.database.friends

import com.blincke.commune_api.models.database.users.CommuneUser
import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne


@Embeddable
data class FriendshipId(
        @ManyToOne
        @JoinColumn(name = "requester", referencedColumnName = "id")
        val requester: CommuneUser,
        @ManyToOne
        @JoinColumn(name = "recipient", referencedColumnName = "id")
        val recipient: CommuneUser,
)