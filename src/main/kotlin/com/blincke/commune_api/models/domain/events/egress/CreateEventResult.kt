package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.database.events.Event

sealed interface CreateEventResult {
    data class Created(val event: Event) : CreateEventResult
    // There will be more cases in the future
}
