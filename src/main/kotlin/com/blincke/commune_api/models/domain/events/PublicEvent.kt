package com.blincke.commune_api.models.domain.events

import com.blincke.commune_api.models.domain.users.PublicUser
import org.joda.time.DateTime

data class PublicEvent(
        val id: String,
        val owner: PublicUser,
        val startDateTime: DateTime,
        val endDateTime: DateTime,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)
