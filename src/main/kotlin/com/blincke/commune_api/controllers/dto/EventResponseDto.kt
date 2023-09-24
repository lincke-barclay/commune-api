package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.Event
import com.blincke.commune_api.models.Visibility
import org.joda.time.DateTime
import java.time.Instant

data class EventResponseDto(
    val id: String,
    val createdTs: Instant,
    val lastUpdatedTs: Instant,
    val owner: CommuneUserResponseDto,
    val venue: LocationResponseDto,
    val visibility: Visibility,
    val isProposal: Boolean,
    val title: String,
    val description: String?,
    val startTime: DateTime?,
    val endTime: DateTime?,
    val attendanceLimit: Int,
    val shareableDegree: Int,
)

fun toEventResponseDto(event: Event): EventResponseDto {
    return with(event) {
        EventResponseDto(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            owner = toCommuneUserResponseDto(owner),
            venue = toLocationResponseDto(venue),
            visibility = visibility,
            isProposal = isProposal,
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            attendanceLimit = attendanceLimit,
            shareableDegree = shareableDegree,
        )
    }
}
