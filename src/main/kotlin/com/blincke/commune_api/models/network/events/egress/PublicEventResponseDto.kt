package com.blincke.commune_api.models.network.events.egress

import com.blincke.commune_api.models.database.events.Event
import java.time.Instant

data class PublicEventResponseDto(
        val id: String,
        val ownerId: String,
        val startDateTime: Instant,
        val endDateTime: Instant,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)

fun Event.toPublicEventResponseDto() = PublicEventResponseDto(
        id = id,
        ownerId = owner.firebaseId,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        title = title,
        shortDescription = shortDescription,
        longDescription = longDescription,
)
