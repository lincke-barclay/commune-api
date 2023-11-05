package com.blincke.commune_api.models.network.events.egress

import com.blincke.commune_api.models.domain.events.PublicEvent
import com.blincke.commune_api.models.network.users.egress.PublicUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto
import java.time.Instant

data class PublicEventResponseDto(
        val id: String,
        val owner: PublicUserResponseDto,
        val startDateTime: Instant,
        val endDateTime: Instant,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)

fun PublicEvent.toPublicEventResponseDto() = PublicEventResponseDto(
        id = id,
        owner = owner.toPublicUserResponseDto(),
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        title = title,
        shortDescription = shortDescription,
        longDescription = longDescription,
)

