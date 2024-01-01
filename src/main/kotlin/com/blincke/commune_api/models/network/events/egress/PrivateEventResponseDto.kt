package com.blincke.commune_api.models.network.events.egress

import com.blincke.commune_api.models.database.events.Event
import java.time.Instant

data class PrivateEventResponseDto(
        val id: String,
        val createdTs: Instant,
        val lastUpdatedTs: Instant,
        val startDateTime: Instant,
        val endDateTime: Instant,
        val title: String,
        val description: String,
)

fun Event.toPrivateEventResponseDto() = PrivateEventResponseDto(
        id = id,
        createdTs = createdTs,
        lastUpdatedTs = lastUpdatedTs,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        title = title,
        description = description,
)

