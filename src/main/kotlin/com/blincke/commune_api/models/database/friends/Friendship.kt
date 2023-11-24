package com.blincke.commune_api.models.database.friends

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "friendship")
data class Friendship(
        @EmbeddedId
        val friendshipId: FriendshipId,

        @Enumerated(EnumType.STRING)
        @Column(name = "status")
        var status: Status,

        @CreationTimestamp
        @Column(name = "created_timestamp_utc", nullable = false, updatable = false)
        val createdTimeStamp: Instant = Instant.now(),

        @UpdateTimestamp
        @Column(name = "last_updated_timestamp_utc", nullable = false, updatable = false)
        var lastUpdatedTS: Instant = Instant.now(),
)
