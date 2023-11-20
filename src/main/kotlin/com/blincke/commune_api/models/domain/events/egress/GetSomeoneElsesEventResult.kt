package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.database.events.Event

sealed interface GetSomeoneElsesEventResult {
    data class Exists(val event: Event) : GetSomeoneElsesEventResult
    data object DoesntExist : GetSomeoneElsesEventResult
}
