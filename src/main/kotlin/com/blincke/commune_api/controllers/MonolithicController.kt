package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.events.egress.CreateEventResult
import com.blincke.commune_api.models.domain.events.egress.DeleteEventResponse
import com.blincke.commune_api.models.domain.events.egress.GetMyEventResult
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.CreateUserResult
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import com.blincke.commune_api.models.network.events.ingress.POSTEventRequestDTO
import com.blincke.commune_api.models.network.users.egress.PublicUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toUserResponseDto
import com.blincke.commune_api.models.network.users.ingress.POSTUserRequestDto
import com.blincke.commune_api.services.EventService
import com.blincke.commune_api.services.FriendService
import com.blincke.commune_api.services.UserService
import org.springframework.data.repository.query.Param
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    @PostMapping("/users")
    fun createUser(
            @RequestBody userRequest: POSTUserRequestDto
    ): ResponseEntity<PublicUserResponseDto> = when (val result = userService.createUser(userRequest)) {
        is CreateUserResult.Created -> ResponseEntity.ok(result.user.toUserResponseDto())
        is CreateUserResult.UnknownFailure -> ResponseEntity.internalServerError().build()
        is CreateUserResult.Conflict -> ResponseEntity.status(HttpStatus.CONFLICT).build()
    }

    @GetMapping("/users")
    fun getUserByEmail(
            @RequestParam userEmail: String
    ): ResponseEntity<PublicUserResponseDto> =
            when (val result = userService.getPublicUserByEmail(userEmail = userEmail)) {
                is GetPublicUserResult.DoesntExist -> ResponseEntity.notFound().build()
                is GetPublicUserResult.Active -> ResponseEntity.ok(result.user.toUserResponseDto())
            }

    @GetMapping("/users/{userId}")
    fun getMyUser(
            @PathVariable("userId") userId: String,
            @AuthenticationPrincipal principal: CommuneUser,
    ) = runAuthorized(principal.id, userId) {
        // Already had to fetch user data when authenticating
        ResponseEntity.ok(principal.toPrivateUser())
    }


    /**
     * SECTION: Events
     */
    @GetMapping("/users/{userId}/events/feed")
    fun getFeedForUser(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        ResponseEntity.ok(eventService.getMyFeed(principal, page, pageSize))
    }

    @GetMapping("/users/{userId}/events/suggested")
    fun getMySuggestedEvents(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        // TODO
        ResponseEntity.ok(eventService.getMySuggestedEvents(principal, page, pageSize))
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    fun getMyEvent(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @PathVariable("eventId") eventId: String,
    ) = runAuthorized(userId, principal.id) {
        when (val result = eventService.getMyEvent(principal, eventId)) {
            is GetMyEventResult.DoesntExist -> ResponseEntity.notFound().build()
            is GetMyEventResult.Exists -> ResponseEntity.ok(result.event)
            is GetMyEventResult.GenericError -> ResponseEntity.internalServerError().build()
            is GetMyEventResult.NotMine -> ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
    }

    @GetMapping("/users/{userId}/events")
    fun getMyEvents(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        ResponseEntity.ok(eventService.getMyEvents(principal, page, pageSize))
    }

    @PostMapping("/users/{userId}/events")
    fun createNewEvent(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @RequestBody createEventRequest: POSTEventRequestDTO,
    ) = runAuthorized(userId, principal.id) {
        when (val result = eventService.createNewEvent(createEventRequest, principal)) {
            is CreateEventResult.Created -> ResponseEntity.ok(result.event)
            is CreateEventResult.GenericError -> ResponseEntity.internalServerError().build()
        }
    }

    @DeleteMapping("/users/{userId}/events/{eventId}")
    fun deleteMyEvent(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @PathVariable("eventId") eventId: String,
    ) = runAuthorized(userId, principal.id) {
        when (eventService.deleteEvent(principal, eventId)) {
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
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = if (principal.id == userId) {
        ResponseEntity.ok(friendService.getMyConfirmedFriends(principal, page, pageSize))
    } else {
        ResponseEntity.ok(friendService.getSomeoneElsesConfirmedFriends(userId, page, pageSize)) // TODO - how should this be restricted if any?
    }

    @GetMapping("/users/{userId}/pending-friends/from-me")
    fun getFriendsIRequested(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        ResponseEntity.ok(friendService.getFriendRequestsISentThatArePending(principal, page, pageSize))
    }

    @GetMapping("/users/{userId}/pending-friends/to-me")
    fun getFriendsRequestedToMe(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        ResponseEntity.ok(friendService.getFriendRequestsSentToMeThatArePending(principal, page, pageSize))
    }

    @GetMapping("/users/{userId}/suggested-friends")
    fun getSuggestedFriendsForMe(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @Param("page") page: Int,
            @Param("pageSize") pageSize: Int,
    ) = runAuthorized(userId, principal.id) {
        ResponseEntity.ok(friendService.getSuggestedFriendsForMe(principal, page, pageSize))
    }

    @PostMapping("/users/{requesterId}/friends/{recipientId}")
    fun transitionOrInitiateFriendship(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("requesterId") requesterId: String,
            @PathVariable("recipientId") recipientId: String,
    ) = runAuthorized(requesterId, principal.id) {
        when (friendService.initiateOrTransitionFriend(principal, recipientId)) {
            is FriendRequestResult.NothingToDo -> ResponseEntity.noContent().build<Unit>()
            is FriendRequestResult.Accepted -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.Created -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.RecipientDoesntExist -> ResponseEntity.notFound().build()
        }
    }


    @DeleteMapping("/users/{requesterId}/friends/{toDeleteId}")
    fun deleteFriendship(
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("requesterId") requesterId: String,
            @PathVariable("toDeleteId") toDeleteId: String,
    ) = runAuthorized(requesterId, principal.id) {
        when (friendService.deleteFriendship(principal, toDeleteId)) {
            is DeleteFriendRequestResult.Succeeded -> ResponseEntity.status(204).build<Unit>()
            is DeleteFriendRequestResult.RecipientDoesntExist -> ResponseEntity.status(404).build()
            is DeleteFriendRequestResult.FriendshipDoesntExist -> ResponseEntity.status(404).build()
        }
    }
}
