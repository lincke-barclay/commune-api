package com.blincke.commune_api.controllers.dto

import com.blincke.commune_api.models.Location
import org.springframework.data.geo.Point
import java.util.*

data class LocationRequestDto(
    val id: String,
    val createdTs: Date,
    val lastUpdatedTs: Date,
    val point: Point,
    val name: String?,
)
