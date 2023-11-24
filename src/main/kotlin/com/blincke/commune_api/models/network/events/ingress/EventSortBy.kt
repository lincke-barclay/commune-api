package com.blincke.commune_api.models.network.events.ingress

enum class PrivateEventSortBy(val column: String) {
    StartDateTime("startDateTime"),
    EndDateTime("endDateTime"),
    CreatedDateTime("createdDateTime"),
    LastUpdatedDateTime("lastUpdatedDateTime"),
}

enum class PublicEventSortBy(val column: String) {
    StartDateTime("startDateTime"),
    EndDateTime("endDateTime"),
}