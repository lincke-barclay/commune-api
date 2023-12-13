package com.blincke.commune_api.services.models

import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.models.network.events.ingress.PublicEventSortBy
import com.blincke.commune_api.repositories.EventRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    fun getEventsOfUser(
        startDateTimeMin: Instant,
        startDateTimeMax: Instant,
        endDateTimeMin: Instant,
        endDateTimeMax: Instant,
        titleContainingIgnoreCase: String,
        owner: User,
        sortBy: PublicEventSortBy,
        sortDirection: SortDirection,
        limit: Int,
    ): List<Event> {
        val pageable = PageRequest.of(0, limit, Sort.by(sortDirection.sortBy, sortBy.column))

        return eventRepository.findAllByStartDateTimeGreaterThanAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndEndDateTimeLessThanAndTitleContainingIgnoreCaseAndOwnerIs(
            startDateTimeMin = startDateTimeMin,
            startDateTimeMax = startDateTimeMax,
            endDateTimeMin = endDateTimeMin,
            endDateTimeMax = endDateTimeMax,
            titleContainingIgnoreCase = titleContainingIgnoreCase,
            owner = owner,
            pageable = pageable,
        )
    }

    fun getEvents(
        startDateTimeMin: Instant,
        startDateTimeMax: Instant,
        endDateTimeMin: Instant,
        endDateTimeMax: Instant,
        titleContainingIgnoreCase: String,
        requester: User, // TODO
        sortBy: PublicEventSortBy,
        sortDirection: SortDirection,
        limit: Int,
    ): List<Event> {
        val pageable = PageRequest.of(0, limit, Sort.by(sortDirection.sortBy, sortBy.column))

        return eventRepository.findAllByStartDateTimeGreaterThanEqualAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndTitleContainingIgnoreCaseAndOwnerNot(
            startDateTimeMin = startDateTimeMin,
            startDateTimeMax = startDateTimeMax,
            endDateTimeMin = endDateTimeMin,
            endDateTimeMax = endDateTimeMax,
            ownerNot = requester,
            titleContainingIgnoreCase = titleContainingIgnoreCase,
            pageable = pageable,
        )
    }

    fun getEventById(
        id: String,
    ) = eventRepository.findByIdOrNull(id)?.let {
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

    fun deleteEvent(
        event: Event,
    ) = eventRepository.delete(event)
}
