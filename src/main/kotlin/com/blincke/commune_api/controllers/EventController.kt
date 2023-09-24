package com.blincke.commune_api.controllers

import com.blincke.commune_api.controllers.dto.*
import com.blincke.commune_api.exceptions.UserNotFoundException
import com.blincke.commune_api.services.EventService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.lang.Exception

@RestController
@RequestMapping("events")
class EventController(
    private val eventService: EventService,
) {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping
    fun createEvent(
        @RequestBody eventRequestDto: EventRequestDto,
    ): ResponseEntity<EventResponseDto> {
        return try {
            ResponseEntity.ok(
                toEventResponseDto(
                    with(eventRequestDto) {
                        eventService.createEvent(
                            owner = owner,
                            venuePoint = venuePoint,
                            visibility = visibility,
                            concrete = concrete,
                            title = title,
                            description = description,
                            startTime = startTime,
                            endTime = endTime,
                            attendanceLimit = attendanceLimit,
                            shareableDegree = shareableDegree,
                        )
                    }
                )
            )
        } catch (e: Exception) {
            log.warn("Could not create new user with error", e)
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping
    fun getAllUserOwnedEvents(
        @RequestParam communeUserId: String,
    ): ResponseEntity<List<EventResponseDto>> {
        return try {
            ResponseEntity.ok(
                eventService.getEventsForOwnerById(communeUserId).map {
                    toEventResponseDto(it)
                }
            )
        } catch (e: Exception) {
            log.warn("Could not get new user with error", e)
            return when (e) {
                is UserNotFoundException -> ResponseEntity(HttpStatus.NOT_FOUND)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
    }
}