package com.blincke.commune_api.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import org.springframework.data.geo.Point
import java.time.Instant
import java.util.*

@Entity
class Location(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Instant = Instant.now(),

    @Column
    val point: Point, // Latitude, Longitude

    @Column
    val name: String? = null,
)
