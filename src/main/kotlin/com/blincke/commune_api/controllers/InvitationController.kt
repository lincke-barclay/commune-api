package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthenticated
import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.domain.invitations.egress.CreateInvitationResult
import com.blincke.commune_api.models.domain.invitations.egress.PatchInvitationResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.invitations.egress.toPrivateInvitationResponseDto
import com.blincke.commune_api.models.network.invitations.ingress.InvitationSortBy
import com.blincke.commune_api.services.models.InvitationService
import com.blincke.commune_api.services.models.UserService
import com.github.fge.jsonpatch.JsonPatch
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
class InvitationController(
    private val invitationService: InvitationService,
    private val userService: UserService,
) {
    private val logger = AppLoggerFactory.getLogger(this.javaClass)

    @GetMapping("/users/{userId}/invitations")
    fun getMyInvitations(
        principal: JwtAuthenticationToken,
        @PathVariable("userId") userId: String,
        @RequestParam("expirationTimestampINC", required = false) expirationTimestampInclusive: Instant? = null,
        @RequestParam("sortBy", required = false) sortBy: InvitationSortBy? = null,
        @RequestParam("sortDirection", required = false) sortDirection: SortDirection? = null,
        @RequestParam("limit", required = true) limit: Int,
        @RequestParam("status", required = true) status: Status,
    ): ResponseEntity<out Any> {
        logger.debug("Getting all invitations for user $userId")
        return userService.runAuthorized(userId, principal) { requestingUser ->
            val results = invitationService.getAllInvitationsForRecipient(
                recipient = requestingUser,
                sortBy = sortBy ?: InvitationSortBy.ExpirationTimestamp,
                expirationTimestamp = expirationTimestampInclusive ?: Instant.now(),
                sortDirection = SortDirection.ASC,
                limit = limit,
                status = status,
            ).map { it.toPrivateInvitationResponseDto() }
            ResponseEntity.ok(results)
        }
    }

    @PostMapping("/users/{recipientId}/invitations")
    fun createNewInvitation(
        principal: JwtAuthenticationToken,
        @PathVariable("recipientId", required = true) recipientId: String,
        @RequestParam("eventId", required = true) eventId: String,
        @RequestParam("expirationTimestamp", required = false) expirationTimestamp: Instant? = null,
    ): ResponseEntity<out Any> {
        logger.debug("Creating an invitation for user $recipientId")
        return userService.runAuthenticated(principal) { requestingUser ->

            val result = invitationService.createNewInvitationForUserAndEventByIds(
                sender = requestingUser,
                recipientId = recipientId,
                eventId = eventId,
                expirationTimestamp = expirationTimestamp,
            )

            when (result) {
                is CreateInvitationResult.Created -> ResponseEntity.ok(result.invitation.toPrivateInvitationResponseDto())
                CreateInvitationResult.NoEvent -> ResponseEntity.notFound().build()
                CreateInvitationResult.NoRecipient -> ResponseEntity.notFound().build()
                CreateInvitationResult.NotFriends -> ResponseEntity.notFound().build()
            }
        }
    }

    @PatchMapping(path = ["/users/{userId}/invitations/{invitationId}"], consumes = ["application/json-patch+json"])
    fun patchInvitation(
        principal: JwtAuthenticationToken,
        @PathVariable("userId", required = true) userId: String,
        @PathVariable("invitationId", required = true) invitationId: String,
        @RequestBody patch: JsonPatch,
    ): ResponseEntity<out Any> {
        logger.debug("User $userId is patching invitation $invitationId")
        return userService.runAuthorized(userId, principal) { requestingUser ->
            val result = invitationService.patchInvitation(
                invitationId = invitationId,
                requestingUser = requestingUser,
                jsonPatch = patch,
            )

            when (result) {
                is PatchInvitationResult.Patched -> ResponseEntity.ok(result)
                PatchInvitationResult.InvalidPatch -> ResponseEntity.badRequest().build()
                PatchInvitationResult.NoRecipient -> ResponseEntity.notFound().build()
                PatchInvitationResult.NoInvitation -> ResponseEntity.notFound().build()
            }
        }
    }
}