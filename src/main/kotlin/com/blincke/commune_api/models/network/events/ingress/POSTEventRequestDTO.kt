package com.blincke.commune_api.models.network.events.ingress

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.CommuneUser
import org.joda.time.DateTime

data class POSTEventRequestDTO(
        val title: String,
        val shortDescription: String,
        val longDescription: String,
        val startingDateTime: DateTime,
        val endingDateTime: DateTime
) {
    fun toDomain(owner: CommuneUser) = Event(
            owner = owner,
            startDateTime = startingDateTime,
            endDateTime = endingDateTime,
            title = title,
            shortDescription = shortDescription,
            longDescription = longDescription,
    )
}
