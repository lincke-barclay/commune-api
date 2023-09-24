package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.models.Visibility
import org.joda.time.DateTime
import org.springframework.data.geo.Point

data class EventRequestDto(
    val owner: CommuneUser,
    val venuePoint: Point,
    val visibility: Visibility,
    val concrete: Boolean,
    val title: String,
    val description: String?,
    val startTime: DateTime?,
    val endTime: DateTime?,
    val attendanceLimit: Int?,
    val shareableDegree: Int?,
)
