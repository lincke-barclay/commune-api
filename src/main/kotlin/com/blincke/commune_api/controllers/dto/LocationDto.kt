package com.blincke.commune_api.controllers.dto

import org.springframework.data.geo.Point
import java.util.*

data class LocationDto(
    val id: String,
    val createdTs: Date,
    val lastUpdatedTs: Date,
    val point: Point,
    val name: String?,
)
