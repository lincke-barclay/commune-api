package com.blincke.commune_api.models.domain.events.egress

import com.blincke.commune_api.models.domain.events.PrivateEvent

sealed interface GetMyEventResult {
    data class Exists(val event: PrivateEvent) : GetMyEventResult
    object DoesntExist : GetMyEventResult
    object NotMine : GetMyEventResult
    object GenericError : GetMyEventResult
}
