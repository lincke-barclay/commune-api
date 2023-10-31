package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.CommuneUser
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface EventRepository : JpaRepository<Event, String> {
    fun findFirstById(id: String): Event?
    fun findAllByOwner(owner: CommuneUser, pageable: Pageable): List<Event>
    fun findAllByOwnerNot(owner: CommuneUser, pageable: Pageable): List<Event>
}
