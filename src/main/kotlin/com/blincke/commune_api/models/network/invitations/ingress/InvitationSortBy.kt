package com.blincke.commune_api.models.network.invitations.ingress

enum class InvitationSortBy(val column: String) {
    Sender("sender"),
    Recipient("recipient"),
    Status("status"),
    ExpirationTimestamp("expirationTimestamp"),
}