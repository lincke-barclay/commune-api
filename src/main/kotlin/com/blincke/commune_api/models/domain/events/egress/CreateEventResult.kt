package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.domain.events.PrivateEvent

sealed interface CreateEventResult {
    data class Created(val event: PrivateEvent) : CreateEventResult
    object GenericError : CreateEventResult
}
