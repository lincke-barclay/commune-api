package com.blincke.commune_api.controllers.dto

import org.joda.time.DateTime
import org.springframework.data.geo.Point

data class LocationResponseDto(
    val id: String,
    val createdTs: DateTime,
    val lastUpdatedTs: DateTime,
    val point: Point,
    val name: String?,
)
