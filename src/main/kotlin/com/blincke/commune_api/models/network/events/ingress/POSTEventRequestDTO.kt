package com.blincke.commune_api.models.network.events.ingress

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import java.time.Instant

data class POSTEventRequestDTO(
        val title: String,
        val description: String,
        val startingDateTime: Instant,
        val endingDateTime: Instant
) {
    fun toDomain(owner: User) = Event(
            owner = owner,
            startDateTime = startingDateTime,
            endDateTime = endingDateTime,
            title = title,
            description = description,
    )
}
