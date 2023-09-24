package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.time.Instant
import java.util.*

@Entity
class Event(
    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: Instant = Instant.now(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: Instant = Instant.now(),

    @ManyToOne
    val owner: CommuneUser,

    @ManyToOne // FIXME: May become a problem later with multi-venue events
    val venue: Location,

    @Enumerated(EnumType.STRING)
    @Column
    val visibility: Visibility,

    @Column(nullable = false)
    val isProposal: Boolean, // Whether an event is abstract (a proposal) or not

    @Column(nullable = false)
    val title: String,

    @Column
    val description: String? = null,

    @Column
    val startTime: DateTime? = null, // Will be null for proposed events

    @Column
    val endTime: DateTime? = null, // Will be null for proposed events

    @Column(nullable = false)
    val attendanceLimit: Int = 0, // Zero means unlimited attendance

    @Column(nullable = false)
    val shareableDegree: Int = 0, // Governs across how many social degrees the event can be shared
)