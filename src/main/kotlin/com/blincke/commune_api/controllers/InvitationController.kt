package com.blincke.commune_api.controllers

import com.amazonaws.Response
import com.blincke.commune_api.common.largestPSQLDate
import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.domain.invitations.egress.CreateInvitationResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.events.egress.toPrivateEventResponseDto
import com.blincke.commune_api.models.network.events.ingress.PublicEventSortBy
import com.blincke.commune_api.models.network.invitations.egress.toInvitationResponseDto
import com.blincke.commune_api.models.network.invitations.ingress.InvitationSortBy
import com.blincke.commune_api.services.models.InvitationService
import com.blincke.commune_api.services.models.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class InvitationController(
    private val invitationService: InvitationService,
    private val userService: UserService,
) {
    private val logger = AppLoggerFactory.getLogger(this)

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
            ).map { it.toInvitationResponseDto() }
            ResponseEntity.ok(results)
        }
    }

    @PostMapping("/users/{userId}/invitations")
    fun createNewInvitation(
        principal: JwtAuthenticationToken,
        @PathVariable("userId", required = true) userId: String,
        @RequestParam("eventId", required = true) eventId: String,
        @RequestParam("expirationTimestamp", required = false) expirationTimestamp: Instant? = null,
    ): ResponseEntity<out Any> {
        logger.debug("Creating an invitation")
        return userService.runAuthorized(userId, principal) { requestingUser ->
            val result = invitationService.createNewInvitationForUserAndEventByIds(
                sender = requestingUser,
                recipientId = userId,
                eventId = eventId,
                expirationTimestamp = expirationTimestamp,
            )

            when (result) {
                is CreateInvitationResult.Created -> ResponseEntity.ok(result)
                CreateInvitationResult.NoEvent -> ResponseEntity.notFound().build()
                CreateInvitationResult.NoRecipient -> ResponseEntity.notFound().build()
            }
        }
    }
}