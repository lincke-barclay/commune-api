package com.blincke.commune_api.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.time.Instant
import java.util.*

@Entity
class Comment(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Instant = Instant.now(),

    @ManyToOne
    val communeUser: CommuneUser,

    @Column(nullable = false)
    val text: String,

    @ManyToOne
    val event: Event,
) {
}