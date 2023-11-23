package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.users.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface EventRepository : JpaRepository<Event, String> {
    fun findAllByOwner(owner: User, pageable: Pageable): List<Event>

    fun findAllByOwnerNotAndStartDateTimeGreaterThanOrderByStartDateTimeAsc(
        ownerNot: User,
        startDateTime: Instant,
        pageable: Pageable
    ): List<Event>
}
