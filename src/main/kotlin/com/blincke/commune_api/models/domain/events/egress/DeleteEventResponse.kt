package com.blincke.commune_api.models.domain.events.egress

sealed interface DeleteEventResponse {
    object Deleted : DeleteEventResponse
    object NotMine : DeleteEventResponse
    object DoesntExist : DeleteEventResponse
    object GenericError : DeleteEventResponse
}