package com.blincke.commune_api.models

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.joda.time.DateTime
import java.util.*

@Entity
class Event(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdTs: DateTime = DateTime(),

    @UpdateTimestamp
    @Column(nullable = false)
    val lastUpdatedTs: DateTime = DateTime(),

    @ManyToOne
    val owner: CommuneUser,

    @ManyToOne // FIXME: May become a problem later with multi-venue events
    val venue: Location,

    @Enumerated(EnumType.STRING)
    @Column
    val visibility: Visibility,

    @Column(nullable = false)
    val startTime: DateTime,

    @Column(nullable = false)
    val endTime: DateTime,

    @Column
    val title: String,

    @Column
    val description: String? = null,

    @Column
    val attendanceLimit: Int = 0, // Zero means unlimited attendance

    @Column(nullable = false)
    val concrete: Boolean, // Whether an event is theoretical or not

    @Column
    val shareableDegree: Int, // Governs across how many social degrees the event can be shared
) {
}