package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.common.runAuthorizedOrElse
import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.DeleteEventResponse
import com.blincke.commune_api.models.domain.events.egress.GetMyEventResult
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import com.blincke.commune_api.models.network.events.egress.toPrivateEventResponseDto
import com.blincke.commune_api.models.network.events.egress.toPublicEventResponseDto
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.models.network.users.egress.toPrivateUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto
import com.blincke.commune_api.services.EventService
import com.blincke.commune_api.services.FriendService
import com.blincke.commune_api.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

// TODO - split this into sub controllers like this https://stackoverflow.com/questions/40328835/sub-resources-in-spring-rest
@RestController
@RequestMapping("/")
class MonolithicController(
        private val userService: UserService,
        private val friendService: FriendService,
        private val eventService: EventService,
) {
    /**
     * SECTION: users
     */
    @GetMapping("/users/{userId}")
    fun getMyUser(
            @PathVariable("userId") userId: String,
            principal: JwtAuthenticationToken,
    ) = userService.runAuthorizedOrElse(
            userId,
            principal,
            authorizedBody = { me ->
                ResponseEntity.ok(me.toPrivateUser().toPrivateUserResponseDto())
            },
            unauthorizedBody = { _ ->
                when (val notMeResult = userService.getPublicUserById(userId)) {
                    is GetPublicUserResult.Active -> ResponseEntity.ok(notMeResult.user.toPublicUserResponseDto())
                    is GetPublicUserResult.DoesntExist -> ResponseEntity.notFound().build()
                }
            }
    )

    /**
     * SECTION: Events
     */
    @GetMapping("/users/{userId}/events/feed")
    fun getMyFeed(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(eventService.getMyFeed(it, page, pageSize)
                .map { event -> event.toPublicEventResponseDto() })
    }

    @GetMapping("/users/{userId}/events/suggested")
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

    @GetMapping("users/{userId}/events/{eventId}")
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

    @GetMapping("/users/{userId}/events")
    fun getMyEvents(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(eventService.getMyEvents(it, page, pageSize)
                .map { event -> event.toPrivateEventResponseDto() })
    }

    @PostMapping("/users/{userId}/events")
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

    @DeleteMapping("/users/{userId}/events/{eventId}")
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

    /**
     * FRIENDS
     */
    @GetMapping("/users/{userId}/friends")
    fun getConfirmedFriends(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = if (principal.firebaseId == userId) {
        ResponseEntity.ok(friendService.getMyConfirmedFriends(principal, page, pageSize))
    } else {
        ResponseEntity.ok(friendService.getSomeoneElsesConfirmedFriends(userId, page, pageSize)) // TODO - how should this be restricted if any?
    }

    @GetMapping("/users/{userId}/pending-friends/from-me")
    fun getFriendsIRequested(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getFriendRequestsISentThatArePending(it, page, pageSize))
    }

    @GetMapping("/users/{userId}/pending-friends/to-me")
    fun getFriendsRequestedToMe(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getFriendRequestsSentToMeThatArePending(it, page, pageSize))
    }

    @GetMapping("/users/{userId}/suggested-friends")
    fun getSuggestedFriendsForMe(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getSuggestedFriendsForMe(it, page, pageSize))
    }

    @PostMapping("/users/{requesterId}/friends/{recipientId}")
    fun transitionOrInitiateFriendship(
            principal: JwtAuthenticationToken,
            @PathVariable("requesterId") requesterId: String,
            @PathVariable("recipientId") recipientId: String,
    ) = userService.runAuthorized(requesterId, principal) {
        when (friendService.initiateOrTransitionFriend(it, recipientId)) {
            is FriendRequestResult.NothingToDo -> ResponseEntity.noContent().build<Unit>()
            is FriendRequestResult.Accepted -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.Created -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.RecipientDoesntExist -> ResponseEntity.notFound().build()
        }
    }


    @DeleteMapping("/users/{requesterId}/friends/{toDeleteId}")
    fun deleteFriendship(
            principal: JwtAuthenticationToken,
            @PathVariable("requesterId") requesterId: String,
            @PathVariable("toDeleteId") toDeleteId: String,
    ) = userService.runAuthorized(requesterId, principal) {
        when (friendService.deleteFriendship(it, toDeleteId)) {
            is DeleteFriendRequestResult.Succeeded -> ResponseEntity.status(204).build<Unit>()
            is DeleteFriendRequestResult.RecipientDoesntExist -> ResponseEntity.status(404).build()
            is DeleteFriendRequestResult.FriendshipDoesntExist -> ResponseEntity.status(404).build()
        }
    }
}
