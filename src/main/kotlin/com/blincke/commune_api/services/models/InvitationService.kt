package com.blincke.commune_api.services.models

import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.domain.invitations.egress.CreateInvitationResult
import com.blincke.commune_api.models.domain.invitations.egress.PatchInvitationResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.invitations.ingress.InvitationSortBy
import com.blincke.commune_api.repositories.InvitationRepository
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.fge.jsonpatch.JsonPatch
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val objectMapper: ObjectMapper,
    private val friendsService: FriendService,
) {
    private val logger = AppLoggerFactory.getLogger(this.javaClass)

    fun createNewInvitationForUserAndEventByIds(
        sender: User,
        recipientId: String,
        eventId: String,
        expirationTimestamp: Instant?,
    ): CreateInvitationResult {
        // Check if recipient exists
        val recipient = when (val result = userService.getUserById(id = recipientId)) {
            is GetUserResult.Active -> result.user
            GetUserResult.DoesntExist -> return CreateInvitationResult.NoRecipient
        }

        // Check if they're friends first
        if (!friendsService
                .getFriendshipState(sender, recipient)
                .areFullyFriends()
        ) {
            return CreateInvitationResult.NotFriends
        }

        // Get the event in question
        val event = when (val result = eventService.getEventById(id = eventId)) {
            is GetEventResult.Exists -> result.event
            GetEventResult.DoesntExist -> return CreateInvitationResult.NoEvent
        }

        var newExpirationTimeStamp: Instant
        // Verify expiration Time stamp
        if (expirationTimestamp == null) {
            logger.debug("Setting expiration timestamp to event start time")
            newExpirationTimeStamp = event.startDateTime
        } else if (expirationTimestamp > event.startDateTime) {
            logger.debug("Expiration is greater than event start time - setting to event start time")
            newExpirationTimeStamp = event.startDateTime
        } else {
            newExpirationTimeStamp = expirationTimestamp
        }

        // Create a new invitation
        val invitation = createNewInvitation(
            event = event,
            sender = sender,
            recipient = recipient,
            expirationTimestamp = newExpirationTimeStamp,
        )

        return CreateInvitationResult.Created(invitation = invitation)
    }

    fun createNewInvitation(
        event: Event,
        sender: User,
        recipient: User,
        expirationTimestamp: Instant,
        status: Status = Status.PENDING,
    ): Invitation {
        return invitationRepository.save(
            Invitation(
                event = event,
                recipient = recipient,
                sender = sender,
                status = status,
                expirationTimestamp = expirationTimestamp,
            )
        )
    }

    fun getAllInvitationsForRecipient(
        recipient: User,
        status: Status,
        expirationTimestamp: Instant,
        sortBy: InvitationSortBy,
        sortDirection: SortDirection,
        limit: Int,
    ): List<Invitation> {
        val pageable = PageRequest.of(0, limit, Sort.by(sortDirection.sortBy, sortBy.column))
        return invitationRepository.findAllByStatusAndExpirationTimestampBeforeAndRecipientIs(
            recipient = recipient,
            status = status,
            expirationTimestamp = expirationTimestamp,
            pageable = pageable,
        )
    }

    /**
     * Assumptions:
     * You can invite yourself to events you have created. You are not required to attend events you create.
     * You cannot accept invitations on behalf of someone else.
     */
    fun patchInvitation(
        invitationId: String,
        requestingUser: User,
        jsonPatch: JsonPatch,
    ): PatchInvitationResult {
        val targetInvitation =
            invitationRepository.findByIdAndRecipient(invitationId = invitationId, recipient = requestingUser) ?: run {
                logger.warn("Invitation $invitationId was not found for recipient ${requestingUser.firebaseId}")
                return PatchInvitationResult.NoInvitation
            }

        val patchedInvitation = jsonPatch.apply(objectMapper.convertValue(targetInvitation, JsonNode::class.java))
        val updatedInvitation = objectMapper.treeToValue(patchedInvitation, Invitation::class.java)

        try {
            assertValidPatch(oldInvitation = targetInvitation, updatedInvitation = updatedInvitation)
        } catch (e: IllegalArgumentException) {
            return PatchInvitationResult.InvalidPatch
        }

        return PatchInvitationResult.Patched(invitationRepository.save(updatedInvitation))
    }


    /**
     * Cannot change: id, createdTs, lastUpdatedTs, event, sender, recipient
     */
    private fun assertValidPatch(oldInvitation: Invitation, updatedInvitation: Invitation) {
        if (oldInvitation.id != updatedInvitation.id) {
            throw IllegalArgumentException("Invalid patch: cannot change invitation ID")
        }

        if (oldInvitation.createdTs != updatedInvitation.createdTs) {
            throw IllegalArgumentException("Invalid patch: cannot change invitation creation timestamp")
        }

        if (oldInvitation.lastUpdatedTs != updatedInvitation.lastUpdatedTs) {
            throw IllegalArgumentException("Invalid patch: cannot change invitation creation timestamp")
        }

        if (oldInvitation.event != updatedInvitation.event) {
            throw IllegalArgumentException("Invalid patch: cannot change invitation event")
        }

        if (oldInvitation.recipient != updatedInvitation.recipient) {
            throw IllegalArgumentException("Invalid patch: cannot change invitation recipient")
        }
    }
}