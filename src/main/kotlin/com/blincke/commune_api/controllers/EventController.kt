package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.network.events.egress.toMinimalPublicEventListDto
import com.blincke.commune_api.models.network.events.egress.toPrivateEventResponseDto
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.services.models.EventService
import com.blincke.commune_api.services.models.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/events")
class EventController(
    private val userService: UserService,
    private val eventService: EventService,
) {
    @GetMapping
    fun getMyEvents(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
        @RequestParam("queryStr", required = true) queryStr: String,
    ) = userService.runAuthorized(userId, principal) { requestingUser ->
        // TODO - integrate query String
        ResponseEntity.ok(eventService.getEventsOfUser(requestingUser, page, pageSize)
            .map { event -> event.toPrivateEventResponseDto() })
    }

    @PostMapping
    fun createNewEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestBody createEventRequest: POSTEventRequestDTO,
    ) = userService.runAuthorized(userId, principal) { requestingUser ->
        when (val result = eventService.createNewEvent(createEventRequest, requestingUser)) {
            else -> ResponseEntity.ok(result.event.toPrivateEventResponseDto())
        }
    }

    @GetMapping("/feed")
    fun getMyFeed(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) { requestingUser ->
        AppLoggerFactory.getLogger(this).debug("Request to get feed from user with id: ${requestingUser.firebaseId}")
        ResponseEntity.ok(
            eventService.getMyFeed(requestingUser, page, pageSize)
                .toMinimalPublicEventListDto()
        )
    }

    @GetMapping("/suggested")
    fun getMySuggestedEvents(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
    ) = run {
        AppLoggerFactory.getLogger(this).debug("Request to get suggested events for user $userId")
        userService.runAuthorized(userId, principal) { requestingUser ->
            AppLoggerFactory.getLogger(this).debug("Getting suggested events for authenticated user: $userId")
            ResponseEntity.ok(
                eventService.getMySuggestedEvents(requestingUser, page, pageSize)
                    .toMinimalPublicEventListDto()
            )
        }
    }

    @GetMapping("/{eventId}")
    fun getMyEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(userId, principal) { requestingUser ->
        when (val result = eventService.getEventById(eventId)) {
            is GetEventResult.DoesntExist -> ResponseEntity.notFound().build()
            is GetEventResult.Exists -> {
                runAuthorized(result.event.owner.firebaseId, requestingUser) {
                    ResponseEntity.ok(result.event.toPrivateEventResponseDto())
                }
            }
        }
    }

    @DeleteMapping("/{eventId}")
    fun deleteMyEvent(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(userId, principal) { requestingUser ->
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
