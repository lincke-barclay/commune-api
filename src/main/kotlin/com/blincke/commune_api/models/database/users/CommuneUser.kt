package com.blincke.commune_api.models.database.users

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.domain.users.PrivateUser
import com.blincke.commune_api.models.domain.users.PublicUser
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "commune_user")
class CommuneUser(
        @Id
        val id: String = UUID.randomUUID().toString(),

        @CreationTimestamp
        @Column(name = "created_timestamp_utc", nullable = false, updatable = false)
        val createdTs: Instant = Instant.now(),

        @UpdateTimestamp
        @Column(name = "last_updated_timestamp_utc", nullable = false)
        val lastUpdatedTs: Instant = Instant.now(),

        @Column(name = "email", nullable = false, unique = true)
        val email: String,

        @Column(name = "first_name", nullable = false)
        val firstName: String,

        @Column(name = "last_name", nullable = false)
        val lastName: String,

        @OneToMany(mappedBy = "friendshipId.requester")
        val requestedFriends: Set<Friendship>,

        @OneToMany(mappedBy = "friendshipId.recipient")
        val recipientToFriendRequests: Set<Friendship>
) {
    fun toPublicUser() = PublicUser(
            id = id,
            firstName = firstName,
            lastName = lastName,
    )

    fun toPrivateUser() = PrivateUser(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            email = email,
            firstName = firstName,
            lastName = lastName,
    )
}