package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.CommuneUser
import com.blincke.commune_api.models.Event
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository:JpaRepository<Event, String> {
    fun getAllByOwner(owner: CommuneUser): List<Event>
}