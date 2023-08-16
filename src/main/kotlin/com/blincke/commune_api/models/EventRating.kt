package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
class EventRating(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Date = Date(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Date = Date(),

    @ManyToOne
    val submitter: User,

    @ManyToOne
    val event: Event,

    @Column
    val explanation: String? = null,
) {
}