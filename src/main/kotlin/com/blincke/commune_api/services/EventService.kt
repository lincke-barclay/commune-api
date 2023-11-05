package com.blincke.commune_api.services

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.repositories.EventRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class EventService(
        private val eventRepository: EventRepository,
) {
    fun getEventsOfUser(
            requester: User,
            page: Int,
            pageSize: Int,
    ) = eventRepository.findAllByOwner(requester, Pageable.ofSize(pageSize).withPage(page))

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
            page: Int,
            pageSize: Int,
    ) = eventRepository.findAllByOwnerNot(requester, Pageable.ofSize(pageSize).withPage(page))

    fun getMySuggestedEvents(
            requester: User,
            page: Int,
            pageSize: Int,
    ) = getMyFeed(requester, page, pageSize) // TODO - algorithm

    fun deleteEvent(
            event: Event,
    ) = eventRepository.delete(event)
}
