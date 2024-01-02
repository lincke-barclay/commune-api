package com.blincke.commune_api.models.domain.invitations.egress

import com.blincke.commune_api.models.database.invitations.Invitation

sealed interface CreateInvitationResult {
    data class Created(val invitation: Invitation): CreateInvitationResult
    data object NoRecipient: CreateInvitationResult
    data object NoEvent: CreateInvitationResult
    data object NotFriends: CreateInvitationResult
}