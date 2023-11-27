package com.blincke.commune_api.models.network.invitations.egress

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import java.time.Instant

data class InvitationResponseDto (
    val id: String,
    val createdTs: Instant,
    val lastUpdatedTs: Instant,
    val event: Event,
    val sender: User,
    val status: Status,
    val expirationTimestamp: Instant?,
)

fun Invitation.toInvitationResponseDto() = InvitationResponseDto(
    id = id,
    createdTs = createdTs,
    lastUpdatedTs = lastUpdatedTs,
    event = event,
    sender = sender,
    status = status,
    expirationTimestamp = expirationTimestamp,
)