package com.blincke.commune_api.models.network.events.egress

import com.blincke.commune_api.models.domain.events.PrivateEvent
import com.blincke.commune_api.models.network.users.egress.PrivateUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPrivateUserResponseDto
import java.time.Instant

data class PrivateEventResponseDto(
        val id: String,
        val createdTs: Instant,
        val lastUpdatedTs: Instant,
        val startDateTime: Instant,
        val endDateTime: Instant,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)

fun PrivateEvent.toPrivateEventResponseDto() = PrivateEventResponseDto(
        id = id,
        createdTs = createdTs,
        lastUpdatedTs = lastUpdatedTs,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        title = title,
        shortDescription = shortDescription,
        longDescription = longDescription,
)

