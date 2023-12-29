package com.blincke.commune_api.models.domain.invitations.egress

sealed interface InvitationRequestResult {
    data object NoRecipient: InvitationRequestResult
    data object NoEvent: InvitationRequestResult
}