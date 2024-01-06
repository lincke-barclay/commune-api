package com.blincke.commune_api.models.network.invitations.egress

import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.network.events.egress.PublicEventResponseDto
import com.blincke.commune_api.models.network.events.egress.toPublicEventResponseDto
import java.time.Instant

data class PrivateInvitationResponseDto(
    val id: String,
    val createdTs: Instant,
    val lastUpdatedTs: Instant,
    val event: PublicEventResponseDto,
    val sender: User,
    val status: Status,
    val expirationTimestamp: Instant?,
)

fun Invitation.toPrivateInvitationResponseDto() = PrivateInvitationResponseDto(
    id = id,
    createdTs = createdTs,
    lastUpdatedTs = lastUpdatedTs,
    event = event.toPublicEventResponseDto(),
    sender = sender,
    status = status,
    expirationTimestamp = expirationTimestamp,
)