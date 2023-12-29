package com.blincke.commune_api.models.database.invitations

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "invitation")
class Invitation(
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
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    val event: Event,

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "firebase_id")
    val sender: User,

    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "firebase_id")
    val recipient: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    var status: Status,

    @Column(name = "expiration_timestamp_utc")
    val expirationTimestamp: Instant? = null, // FIXME: default to event start
)