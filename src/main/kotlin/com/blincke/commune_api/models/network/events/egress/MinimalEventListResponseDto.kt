package com.blincke.commune_api.models.network.events.egress

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.network.users.egress.PublicUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto


data class MinimalEventListResponseDto(
        val publicEvents: List<PublicEventResponseDto>,
        val publicUsers: HashMap<String, PublicUserResponseDto>
)

fun List<Event>.toMinimalPublicEventListDto(): MinimalEventListResponseDto {
    val publicUsers = HashMap<String, PublicUserResponseDto>()
    val publicEvents = map { event ->
        if (!publicUsers.containsKey(event.owner.firebaseId)) {
            publicUsers[event.owner.firebaseId] = event.owner.toPublicUserResponseDto()
        }
        event.toPublicEventResponseDto()
    }

    return MinimalEventListResponseDto(
            publicEvents = publicEvents,
            publicUsers = publicUsers,
    )
}
