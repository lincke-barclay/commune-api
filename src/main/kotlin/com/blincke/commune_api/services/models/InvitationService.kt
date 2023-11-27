package com.blincke.commune_api.services.models

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.events.egress.GetEventResult
import com.blincke.commune_api.models.domain.invitations.egress.CreateInvitationResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.models.network.SortDirection
import com.blincke.commune_api.models.network.invitations.ingress.InvitationSortBy
import com.blincke.commune_api.repositories.InvitationRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val eventService: EventService,
    private val userService: UserService,
) {

    fun createNewInvitationForUserAndEventByIds(
        sender: User,
        recipientId: String,
        eventId: String,
        expirationTimestamp: Instant?,
    ): CreateInvitationResult {
        val recipient = when (val result = userService.getUserById(id = recipientId)) {
            is GetUserResult.Active -> result.user
            GetUserResult.DoesntExist -> return CreateInvitationResult.NoRecipient
        }

        val event = when (val result = eventService.getEventById(id = eventId)) {
            is GetEventResult.Exists -> result.event
            GetEventResult.DoesntExist -> return CreateInvitationResult.NoEvent
        }

        val invitation = createNewInvitation(
            event = event,
            sender = sender,
            recipient = recipient,
            expirationTimestamp = expirationTimestamp,
        )

        return CreateInvitationResult.Created(invitation = invitation)
    }

    fun createNewInvitation(
        event: Event,
        sender: User,
        recipient: User,
        expirationTimestamp: Instant? = null,
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
}