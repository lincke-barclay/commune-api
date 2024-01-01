package com.blincke.commune_api.models.domain.invitations.egress

import com.blincke.commune_api.models.database.invitations.Invitation

sealed interface PatchInvitationResult {
    data class Patched(val invitation: Invitation): PatchInvitationResult
    data object InvalidPatch: PatchInvitationResult
    data object NoRecipient: PatchInvitationResult
    data object NoInvitation: PatchInvitationResult
}