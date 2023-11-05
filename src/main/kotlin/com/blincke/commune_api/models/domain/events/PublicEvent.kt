package com.blincke.commune_api.models.domain.events

import com.blincke.commune_api.models.domain.users.PublicUser
import java.time.Instant

data class PublicEvent(
        val id: String,
        val owner: PublicUser,
        val startDateTime: Instant,
        val endDateTime: Instant,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)
