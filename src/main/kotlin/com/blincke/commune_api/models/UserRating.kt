package com.blincke.commune_api.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
class UserRating(
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
    val recipient: User,

    @ManyToOne
    val event: Event,

    @Column
    val explanation: String? = null,
) {
}