package com.blincke.commune_api.models.domain.events.egress

sealed interface DeleteEventResult {
    data object Deleted : DeleteEventResult
    data object DoesntExist : DeleteEventResult
}