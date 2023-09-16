package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.Location
import org.springframework.data.geo.Point
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository: JpaRepository<Location, String> {
    fun getFirstByPoint(point: Point): Location?
}