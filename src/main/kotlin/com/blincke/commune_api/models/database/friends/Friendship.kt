package com.blincke.commune_api.models.database.friends

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity
@Table(name = "friendship")
data class Friendship(
    @EmbeddedId
    val friendshipId: FriendshipId,

    @CreationTimestamp
    @Column(name = "created_timestamp_utc", nullable = false, updatable = false)
    val createdTimeStamp: Instant = Instant.now(),
)
