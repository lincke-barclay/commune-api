package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.database.events.Event

sealed interface GetEventResult {
    data class Exists(val event: Event) : GetEventResult
    data object DoesntExist : GetEventResult
}
