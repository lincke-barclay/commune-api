package com.blincke.commune_api.models.database.events

import com.blincke.commune_api.models.database.users.User
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
    var lastUpdatedTs: Instant = Instant.now(),

    @ManyToOne
    @JoinColumn(name = "firebase_owner_id", referencedColumnName = "firebase_id")
    val owner: User,

    @Column(name = "starting_timestamp_utc", nullable = false)
    val startDateTime: Instant,

    @Column(name = "ending_timestamp_utc", nullable = false)
    val endDateTime: Instant,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "short_description", nullable = false)
    val shortDescription: String,

    @Column(name = "long_description", nullable = false)
    val longDescription: String,
)

