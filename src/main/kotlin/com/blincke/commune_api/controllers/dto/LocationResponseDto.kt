package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.Location
import org.springframework.data.geo.Point
import java.time.Instant

data class LocationResponseDto(
    val id: String,
    val createdTs: Instant,
    val lastUpdatedTs: Instant,
    val point: Point,
    val name: String?,
)

fun toLocationResponseDto(location: Location): LocationResponseDto {
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
