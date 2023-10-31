package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.database.events.Event

sealed interface GetMyDatabaseEventResult {
    data class Exists(val event: Event) : GetMyDatabaseEventResult
    object DoesntExist : GetMyDatabaseEventResult
    object NotMine : GetMyDatabaseEventResult
    object GenericError : GetMyDatabaseEventResult
}
