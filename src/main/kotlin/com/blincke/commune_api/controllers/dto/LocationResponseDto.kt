package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.Location
import org.joda.time.DateTime
import org.springframework.data.geo.Point

data class LocationResponseDto(
    val id: String,
    val createdTs: DateTime,
    val lastUpdatedTs: DateTime,
    val point: Point,
    val name: String?,
)

fun LocationResponse(location: Location): LocationResponseDto {
    return with(location) {
        LocationResponseDto(
            id = id,
            createdTs = createdTs,
            lastUpdatedTs = lastUpdatedTs,
            name = name,
            point = point,
        )
    }
}
