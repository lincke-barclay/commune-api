package com.blincke.commune_api.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.util.*

@Entity
class UserRating(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: DateTime = DateTime(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: DateTime = DateTime(),

    @ManyToOne
    val submitter: User,

    @ManyToOne
    val recipient: User,

    @ManyToOne
    val event: Event,

    @Column
    val explanation: String? = null,
) {
}