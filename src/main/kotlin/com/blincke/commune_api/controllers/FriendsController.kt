package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.common.runAuthorizedOrElse
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto
import com.blincke.commune_api.services.FriendService
import com.blincke.commune_api.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/friends")
class FriendsController(
    private val friendService: FriendService,
    private val userService: UserService,
) {
    @GetMapping
    fun getConfirmedFriends(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorizedOrElse(
        userId,
        principal,
        authorizedBody = {
            AppLoggerFactory.getLogger(this)
                .debug("Request to get confirmed friends from user with id: ${it.firebaseId}")
            ResponseEntity.ok(
                friendService.getConfirmedFriends(it, page, pageSize)
                    .map { friend -> friend.toPublicUserResponseDto() })
        },
        unauthorizedBody = {
            ResponseEntity.ok(
                friendService.getConfirmedFriendsById(userId, page, pageSize)
                    .map { it.toPublicUserResponseDto() }) // TODO - how should this be restricted if any?
        }
    )

    @GetMapping("/pending/from-me")
    fun getFriendsIRequested(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        AppLoggerFactory.getLogger(this)
            .debug("Request to get pending friends from user with id: ${it.firebaseId}")
        ResponseEntity.ok(friendService.getFriendRequestsUserSentThatArePending(it, page, pageSize)
            .map { friend -> friend.toPublicUserResponseDto() })
    }

    @GetMapping("/pending/to-me")
    fun getFriendsRequestedToMe(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        AppLoggerFactory.getLogger(this)
            .debug("Request to get pending friends for user with id: ${it.firebaseId}")
        ResponseEntity.ok(friendService.getFriendRequestsSentToUserThatArePending(it, page, pageSize)
            .map { friend -> friend.toPublicUserResponseDto() })
    }

    @GetMapping("/suggested")
    fun getSuggestedFriendsForMe(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
        @RequestParam("queryStr", required = true) queryStr: String,
    ) = run {
        // TODO - integrate query String
        AppLoggerFactory.getLogger(this).debug(
            "Request to get suggested friends for user $userId, pageSize = $pageSize, " +
                    "page = $page, queryStr = $queryStr"
        )
        userService.runAuthorized(userId, principal) {
            AppLoggerFactory.getLogger(this).debug("From user with id ${it.firebaseId}")
            ResponseEntity.ok(friendService.getSuggestedFriendsForUser(it, page, pageSize)
                .map { friend -> friend.toPublicUserResponseDto() })
        }
    }

    @PostMapping("/{recipientId}")
    fun transitionOrInitiateFriendship(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") requesterId: String,
        @PathVariable("recipientId") recipientId: String,
    ) = userService.runAuthorized(requesterId, principal) {
        when (friendService.initiateOrTransitionFriend(it, recipientId)) {
            is FriendRequestResult.NothingToDo -> ResponseEntity.noContent().build<Unit>()
            is FriendRequestResult.Accepted -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.Created -> ResponseEntity.status(HttpStatus.CREATED).build()
            is FriendRequestResult.RecipientDoesntExist -> ResponseEntity.notFound().build()
            is FriendRequestResult.RequestedToSameUser -> ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    @DeleteMapping("/{toDeleteId}")
    fun deleteFriendship(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") requesterId: String,
        @PathVariable("toDeleteId") toDeleteId: String,
    ) = userService.runAuthorized(requesterId, principal) {
        when (friendService.deleteFriendship(it, toDeleteId)) {
            is DeleteFriendRequestResult.Succeeded -> ResponseEntity.status(204).build<Unit>()
            is DeleteFriendRequestResult.RecipientDoesntExist -> ResponseEntity.status(404).build()
            is DeleteFriendRequestResult.FriendshipDoesntExist -> ResponseEntity.status(404).build()
        }
    }
}