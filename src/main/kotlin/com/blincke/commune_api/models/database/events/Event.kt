package com.blincke.commune_api.models.database.events

import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.events.PrivateEvent
import com.blincke.commune_api.models.domain.events.PublicEvent
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "event")
class Event(
        @Id
        @Column(name = "id")
        val id: String = UUID.randomUUID().toString(),

        @CreationTimestamp
        @Column(name = "created_timestamp_utc", nullable = false, updatable = false)
        val createdTs: Instant = Instant.now(),

        @UpdateTimestamp
        @Column(name = "last_updated_timestamp_utc", nullable = false)
        val lastUpdatedTs: Instant = Instant.now(),

        @ManyToOne
        @JoinColumn(name = "owner", referencedColumnName = "firebase_id")
        val owner: CommuneUser,

        @Column(name = "starting_datetime_utc", nullable = false)
        val startDateTime: Instant,

        @Column(name = "ending_datetime_utc", nullable = false)
        val endDateTime: Instant,

        @Column(name = "title", nullable = false)
        val title: String,

        @Column(name = "short_description", nullable = false)
        val shortDescription: String,

        @Column(name = "long_description", nullable = false)
        val longDescription: String,
) {
    fun toPublicEvent() = PublicEvent(
            id = id,
            owner = owner.toPublicUser(),
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            title = title,
            shortDescription = shortDescription,
            longDescription = longDescription,
    )

    fun toPrivateEvent() = PrivateEvent(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            owner = owner.toPrivateUser(),
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            title = title,
            shortDescription = shortDescription,
            longDescription = longDescription,
    )
}
