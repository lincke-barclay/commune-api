package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.DeleteEventResponse
import com.blincke.commune_api.models.domain.events.egress.GetMyEventResult
import com.blincke.commune_api.models.network.events.egress.toPrivateEventResponseDto
import com.blincke.commune_api.models.network.events.egress.toPublicEventResponseDto
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.services.EventService
import com.blincke.commune_api.services.UserService
import org.springframework.http.HttpStatus
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
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(eventService.getMyEvents(it, page, pageSize)
                .map { event -> event.toPrivateEventResponseDto() })
    }

    @PostMapping
    fun createNewEvent(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestBody createEventRequest: POSTEventRequestDTO,
    ) = userService.runAuthorized(userId, principal) {
        when (val result = eventService.createNewEvent(createEventRequest, it)) {
            is CreateEventResult.Created -> ResponseEntity.ok(result.event.toPrivateEventResponseDto())
            is CreateEventResult.GenericError -> ResponseEntity.internalServerError().build()
        }
    }

    @GetMapping("/feed")
    fun getMyFeed(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(eventService.getMyFeed(it, page, pageSize)
                .map { event -> event.toPublicEventResponseDto() })
    }

    @GetMapping("/suggested")
    fun getMySuggestedEvents(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        // TODO
        ResponseEntity.ok(eventService.getMySuggestedEvents(it, page, pageSize)
                .map { event -> event.toPublicEventResponseDto() })
    }

    @GetMapping("/{eventId}")
    fun getMyEvent(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(userId, principal) {
        when (val result = eventService.getMyEvent(it, eventId)) {
            is GetMyEventResult.DoesntExist -> ResponseEntity.notFound().build()
            is GetMyEventResult.Exists -> ResponseEntity.ok(result.event.toPrivateEventResponseDto())
            is GetMyEventResult.GenericError -> ResponseEntity.internalServerError().build()
            is GetMyEventResult.NotMine -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @DeleteMapping("/{eventId}")
    fun deleteMyEvent(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @PathVariable("eventId") eventId: String,
    ) = userService.runAuthorized(userId, principal) {
        when (eventService.deleteEvent(it, eventId)) {
            is DeleteEventResponse.Deleted -> ResponseEntity.notFound().build<Unit>()
            is DeleteEventResponse.DoesntExist -> ResponseEntity.notFound().build()
            is DeleteEventResponse.GenericError -> ResponseEntity.internalServerError().build()
            is DeleteEventResponse.NotMine -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }
}
