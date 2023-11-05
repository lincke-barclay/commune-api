package com.blincke.commune_api.models.domain.events

import com.blincke.commune_api.models.domain.users.PrivateUser
import org.joda.time.DateTime
import java.time.Instant

data class PrivateEvent(
        val id: String,
        val createdTs: Instant,
        val lastUpdatedTs: Instant,
        val owner: PrivateUser,
        val startDateTime: DateTime,
        val endDateTime: DateTime,
        val title: String,
        val shortDescription: String,
        val longDescription: String,
)