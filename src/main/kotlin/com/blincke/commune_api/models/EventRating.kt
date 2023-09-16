package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.time.Instant
import java.util.*

@Entity
class EventRating(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Instant = Instant.now(),

    @ManyToOne
    val submitter: CommuneUser,

    @ManyToOne
    val event: Event,

    @Column
    val explanation: String? = null,
) {
}