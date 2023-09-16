package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.*

@Entity
class Friendship(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Instant = Instant.now(),

    @ManyToOne
    val initiator: CommuneUser,

    @ManyToOne
    val recipient: CommuneUser,

    @Enumerated(EnumType.STRING)
    val status: Status,
)