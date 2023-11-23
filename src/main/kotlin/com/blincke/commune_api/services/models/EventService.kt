package com.blincke.commune_api.services.models

import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.repositories.EventRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    fun getEventsOfUser(
        requester: User,
        lastEventId: String?,
        pageSize: Int,
    ) = eventRepository.findAllByOwner(requester, Pageable.ofSize(pageSize))

    fun getEventById(
        eventId: String,
    ) = eventRepository.findByIdOrNull(eventId)?.let {
        GetEventResult.Exists(event = it)
    } ?: GetEventResult.DoesntExist

    fun createNewEvent(
        postEventRequestDTO: POSTEventRequestDTO,
        owner: User,
    ) = CreateEventResult.Created(eventRepository.save(postEventRequestDTO.toDomain(owner)))

    // TODO - algorithm
    fun getMyFeed(
        requester: User,
        lastEventId: String?,
        pageSize: Int,
    ): List<Event> {
        AppLoggerFactory.getLogger(this).debug("Getting feed for user with id ${requester.firebaseId}")
        val startDateTime = lastEventId?.let {
            val event = eventRepository.findByIdOrNull(it)
            event?.startDateTime
        } ?: Instant.now()

        return eventRepository.findAllByOwnerNotAndStartDateTimeGreaterThanOrderByStartDateTimeAsc(
            requester,
            startDateTime,
            Pageable.ofSize(pageSize),
        )
    }

    fun getMySuggestedEvents(
        requester: User,
        lastEventId: String?,
        pageSize: Int,
    ) = getMyFeed(requester, lastEventId, pageSize) // TODO - algorithm

    fun deleteEvent(
        event: Event,
    ) = eventRepository.delete(event)
}
