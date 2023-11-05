package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.services.FriendService
import com.blincke.commune_api.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
            @AuthenticationPrincipal principal: CommuneUser,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = if (principal.firebaseId == userId) {
        ResponseEntity.ok(friendService.getMyConfirmedFriends(principal, page, pageSize))
    } else {
        ResponseEntity.ok(friendService.getSomeoneElsesConfirmedFriends(userId, page, pageSize)) // TODO - how should this be restricted if any?
    }

    @GetMapping("/pending/from-me")
    fun getFriendsIRequested(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getFriendRequestsISentThatArePending(it, page, pageSize))
    }

    @GetMapping("/pending/to-me")
    fun getFriendsRequestedToMe(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getFriendRequestsSentToMeThatArePending(it, page, pageSize))
    }

    @GetMapping("/suggested")
    fun getSuggestedFriendsForMe(
            principal: JwtAuthenticationToken,
            @PathVariable("userId") userId: String,
            @RequestParam("page", required = true) page: Int,
            @RequestParam("pageSize", required = true) pageSize: Int,
    ) = userService.runAuthorized(userId, principal) {
        ResponseEntity.ok(friendService.getSuggestedFriendsForMe(it, page, pageSize))
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