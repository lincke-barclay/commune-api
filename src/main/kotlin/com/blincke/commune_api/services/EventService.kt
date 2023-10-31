package com.blincke.commune_api.services

import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.DeleteEventResponse
import com.blincke.commune_api.models.domain.events.egress.GetMyDatabaseEventResult
import com.blincke.commune_api.models.domain.events.egress.GetMyEventResult
import com.blincke.commune_api.models.domain.users.egress.GetCommuneUserResult
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.repositories.EventRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class EventService(
        private val userService: UserService,
        private val eventRepository: EventRepository,
) {
    fun getMyEvents(
            requester: CommuneUser,
            page: Int,
            pageSize: Int,
    ) = try {
        eventRepository.findAllByOwner(requester, Pageable.ofSize(pageSize).withPage(page))
                .map { it.toPrivateEvent() }
    } catch (e: Exception) {
        listOf()
    }

    fun getMyDatabaseEvent(
            requester: CommuneUser,
            eventId: String,
    ) = try {
        eventRepository.findFirstById(eventId)?.let {
            if (it.owner.id != requester.id) {
                GetMyDatabaseEventResult.NotMine
            } else {
                GetMyDatabaseEventResult.Exists(event = it)
            }
        } ?: GetMyDatabaseEventResult.DoesntExist
    } catch (e: Exception) {
        GetMyDatabaseEventResult.GenericError
    }

    fun getMyEvent(
            requester: CommuneUser,
            eventId: String,
    ) = try {
        eventRepository.findFirstById(eventId)?.let {
            if (it.owner.id != requester.id) {
                GetMyEventResult.NotMine
            } else {
                GetMyEventResult.Exists(event = it.toPrivateEvent())
            }
        } ?: GetMyEventResult.DoesntExist
    } catch (e: Exception) {
        GetMyEventResult.GenericError
    }

    fun getSomeoneElsesEvents(
            theirId: String,
            page: Int,
            pageSize: Int,
    ) = when (val owner = userService.getDatabaseUserById(theirId)) {
        is GetCommuneUserResult.Active -> listOf(eventRepository.findAllByOwner(owner.user, Pageable.ofSize(pageSize).withPage(page))
                .map { it.toPublicEvent() })

        is GetCommuneUserResult.DoesntExist -> listOf()
    }

    fun createNewEvent(
            postEventRequestDTO: POSTEventRequestDTO,
            owner: CommuneUser,
    ) = try {
        CreateEventResult.Created(eventRepository.save(postEventRequestDTO.toDomain(owner)).toPrivateEvent())
    } catch (e: Exception) {
        CreateEventResult.GenericError
    }

    fun getMyFeed(
            requester: CommuneUser,
            page: Int,
            pageSize: Int,
    ) = try {
        eventRepository.findAllByOwnerNot(requester, Pageable.ofSize(pageSize).withPage(page))
                .map { it.toPublicEvent() }
    } catch (e: Exception) {
        listOf()
    }

    fun getMySuggestedEvents(
            requester: CommuneUser,
            page: Int,
            pageSize: Int,
    ) = getMyFeed(requester, page, pageSize) // TODO

    fun deleteEvent(
            requester: CommuneUser,
            eventId: String,
    ) = when (val result = getMyDatabaseEvent(requester, eventId)) {
        is GetMyDatabaseEventResult.NotMine -> DeleteEventResponse.NotMine
        is GetMyDatabaseEventResult.GenericError -> DeleteEventResponse.GenericError
        is GetMyDatabaseEventResult.DoesntExist -> DeleteEventResponse.DoesntExist
        is GetMyDatabaseEventResult.Exists -> {
            try {
                eventRepository.delete(result.event)
                DeleteEventResponse.Deleted
            } catch (e: Exception) {
                DeleteEventResponse.GenericError
            }
        }
    }
}
