package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.largestPSQLDate
import com.blincke.commune_api.common.runAuthenticated
import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.events.egress.toMinimalPublicEventListDto
import com.blincke.commune_api.models.network.events.egress.toPrivateEventResponseDto
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.models.network.events.ingress.PublicEventSortBy
import com.blincke.commune_api.services.models.EventService
import com.blincke.commune_api.services.models.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
class EventController(
    private val userService: UserService,
    private val eventService: EventService,
) {
    private val logger = AppLoggerFactory.getLogger(this)

    @GetMapping("/users/{myId}/events")
    fun getMyEvents(
        principal: JwtAuthenticationToken,
        @PathVariable("myId") myId: String,
        @RequestParam("fromStartDateTimeINC", required = false) fromStartDateTimeInclusive: Instant? = null,
        @RequestParam("fromEndDateTimeINC", required = false) fromEndDateTimeInclusive: Instant? = null,
        @RequestParam("toStartDateTimeINC", required = false) toStartDateTimeInclusive: Instant? = null,
        @RequestParam("toEndDateTimeINC", required = false) toEndDateTimeInclusive: Instant? = null,
        @RequestParam("titleContainsIC", required = false) titleContainsIC: String? = null,
        @RequestParam("sortBy", required = false) sortBy: PublicEventSortBy? = null,
        @RequestParam("sortDirection", required = false) sortDirection: SortDirection? = null,
        @RequestParam("limit", required = true) limit: Int,
    ): ResponseEntity<out Any> {
        return userService.runAuthorized(myId, principal) { requestingUser ->
            val results = eventService.getEventsOfUser(
                startDateTimeMin = fromStartDateTimeInclusive ?: Instant.now(),
                startDateTimeMax = toStartDateTimeInclusive ?: largestPSQLDate(),
                endDateTimeMin = fromEndDateTimeInclusive ?: Instant.now(),
                endDateTimeMax = toEndDateTimeInclusive ?: largestPSQLDate(),
                titleContainingIgnoreCase = titleContainsIC ?: "",
                owner = requestingUser,
                sortBy = sortBy ?: PublicEventSortBy.StartDateTime,
                sortDirection = SortDirection.ASC,
                limit = limit,
            ).map { it.toPrivateEventResponseDto() }

            ResponseEntity.ok(results)
        }
    }

    @PostMapping("/users/{myId}/events")
    fun createNewEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("myId") myId: String,
        @RequestBody createEventRequest: POSTEventRequestDTO,
    ) = userService.runAuthorized(myId, principal) { requestingUser ->
        when (val result = eventService.createNewEvent(createEventRequest, requestingUser)) {
            else -> ResponseEntity.ok(result.event.toPrivateEventResponseDto())
        }
    }

    @GetMapping("/users/{myId}/events/feed")
    fun getMyFeed(
        principal: JwtAuthenticationToken,
        @PathVariable("myId") myId: String,
        @RequestParam("lastEventId", required = false) lastEventId: String?,
        @RequestParam("limit", required = true) limit: Int,
    ): ResponseEntity<out Any> {
        return userService.runAuthorized(myId, principal) { requestingUser ->
            logger.debug(
                "Request to get feed from user with " +
                        "id: ${requestingUser.firebaseId} starting from lastEventId: $lastEventId"
            )
            val results = eventService.getMyFeed(requestingUser, lastEventId, limit)
                .toMinimalPublicEventListDto()

            ResponseEntity.ok(results)
        }
    }

    @GetMapping("/events")
    fun getPublicEvents(
        principal: JwtAuthenticationToken,
        @RequestParam("fromStartDateTimeINC", required = false) fromStartDateTimeInclusive: Instant? = null,
        @RequestParam("fromEndDateTimeINC", required = false) fromEndDateTimeInclusive: Instant? = null,
        @RequestParam("toStartDateTimeINC", required = false) toStartDateTimeInclusive: Instant? = null,
        @RequestParam("toEndDateTimeINC", required = false) toEndDateTimeInclusive: Instant? = null,
        @RequestParam("titleContainsIC", required = false) titleContainsIC: String? = null,
        @RequestParam("sortBy", required = false) sortBy: PublicEventSortBy? = null,
        @RequestParam("sortDirection", required = false) sortDirection: SortDirection? = null,
        @RequestParam("limit", required = true) limit: Int,
    ): ResponseEntity<out Any> {
        return userService.runAuthenticated(principal) { requestingUser ->
            logger.debug("Getting event query results for authenticated user: ${requestingUser.firebaseId}")

            val events = eventService.getEvents(
                startDateTimeMin = fromStartDateTimeInclusive ?: Instant.now(),
                startDateTimeMax = toStartDateTimeInclusive ?: largestPSQLDate(),
                endDateTimeMin = fromEndDateTimeInclusive ?: Instant.now(),
                endDateTimeMax = toEndDateTimeInclusive ?: largestPSQLDate(),
                titleContainingIgnoreCase = titleContainsIC ?: "",
                requester = requestingUser,
                sortBy = sortBy ?: PublicEventSortBy.StartDateTime,
                sortDirection = sortDirection ?: SortDirection.ASC,
                limit = limit,
            ).toMinimalPublicEventListDto()

            ResponseEntity.ok(events)
        }
    }

    @GetMapping("/users/{myId}/events/{eventId}")
    fun getMyEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("myId") myId: String,
        @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(myId, principal) { requestingUser ->
        when (val result = eventService.getEventById(eventId)) {
            is GetEventResult.DoesntExist -> ResponseEntity.notFound().build()
            is GetEventResult.Exists -> {
                runAuthorized(result.event.owner.firebaseId, requestingUser) {
                    ResponseEntity.ok(result.event.toPrivateEventResponseDto())
                }
            }
        }
    }

    @DeleteMapping("/users/{myId}/events/{eventId}")
    fun deleteMyEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("myId") myId: String,
        @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(myId, principal) { requestingUser ->
        when (val getResult = eventService.getEventById(eventId)) {
            is GetEventResult.DoesntExist -> ResponseEntity.notFound().build()
            is GetEventResult.Exists -> {
                runAuthorized(getResult.event.owner.firebaseId, requestingUser) {
                    ResponseEntity.ok(eventService.deleteEvent(getResult.event))
                }
            }
        }
    }
}
