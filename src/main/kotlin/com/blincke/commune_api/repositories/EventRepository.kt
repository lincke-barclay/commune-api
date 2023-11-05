package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface EventRepository : JpaRepository<Event, String> {
    fun findFirstById(id: String): Event?
    fun findAllByOwner(owner: User, pageable: Pageable): List<Event>
    fun findAllByOwnerNot(owner: User, pageable: Pageable): List<Event>
}
