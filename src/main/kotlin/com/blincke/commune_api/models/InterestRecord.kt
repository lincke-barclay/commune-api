package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.util.*

@Entity
class InterestRecord(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: DateTime = DateTime(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: DateTime = DateTime(),

    @ManyToOne
    val proposal: Event,

    @ManyToOne
    val recipient: User,

    @Enumerated(EnumType.STRING)
    val status: Status, // Can only be pending if event is invite-only

    @Column(nullable = false)
    val expiration: DateTime,
) {
}