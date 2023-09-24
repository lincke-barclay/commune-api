package com.blincke.commune_api.services

import com.blincke.commune_api.models.Location
import com.blincke.commune_api.repositories.LocationRepository
import org.springframework.data.geo.Point
import org.springframework.stereotype.Service

@Service
class LocationService(
    private val locationRepository: LocationRepository,
) {
    fun getOrCreateLocationPoint(locationPoint: Point): Location {
        return locationRepository.getFirstByPoint(locationPoint)
            ?: locationRepository.save(Location(point = locationPoint))
    }
}