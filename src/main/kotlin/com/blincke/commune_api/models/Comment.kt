package com.blincke.commune_api.models

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.*

@Entity
class Comment(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Date = Date(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Date = Date(),

    @ManyToOne
    val user: User,

    @Column(nullable = false)
    val text: String,

    @ManyToOne
    val event: Event,
) {
}