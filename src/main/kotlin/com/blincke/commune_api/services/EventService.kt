package com.blincke.commune_api.services

import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.models.Event
import com.blincke.commune_api.models.Visibility
import com.blincke.commune_api.repositories.EventRepository
import org.joda.time.DateTime
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

@Service
class EventService(
    private val communeUserService: CommuneUserService,
    private val eventRepository: EventRepository,
    private val locationService: LocationService,
) {
    fun createEvent(
        owner: CommuneUser,
        venuePoint: Point,
        visibility: Visibility,
        startTime: DateTime?,
        endTime: DateTime?,
        title: String,
        concrete: Boolean,
        description: String?,
        attendanceLimit: Int?,
        shareableDegree: Int?,
    ): Event {
        val venueLocation = locationService.getOrCreateLocationPoint(locationPoint = venuePoint)
        return eventRepository.save(
            Event(
                owner = owner,
                venue = venueLocation,
                visibility = visibility,
                startTime = startTime,
                endTime = endTime,
                title = title,
                isProposal = concrete,
                description = description,
                attendanceLimit = attendanceLimit ?: 0,
                shareableDegree = shareableDegree ?: 0,
                )
        )
    }

    @Throws(UserNotFoundException::class)
    fun getEventsForOwnerById(ownerId: String): List<Event> {
        return eventRepository.getAllByOwner(owner = communeUserService.getUserById(ownerId))
    }
}