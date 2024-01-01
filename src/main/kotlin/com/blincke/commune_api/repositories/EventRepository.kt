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

    /**
     * startDt \in [[startDateTimeMin], [startDateTimeMax]]
     * endDt \in [[endDateTimeMin], [endDateTimeMax]]
     * title containing ignore case [titleContainingIgnoreCase]
     * owner is [owner]
     * ORDER BY LIMIT [pageable]
     */
    fun findAllByStartDateTimeGreaterThanAndStartDateTimeLessThanAndEndDateTimeGreaterThanAndEndDateTimeLessThanAndTitleContainingIgnoreCaseAndOwnerIs(
        startDateTimeMin: Instant,
        startDateTimeMax: Instant,
        endDateTimeMin: Instant,
        endDateTimeMax: Instant,
        titleContainingIgnoreCase: String,
        owner: User,
        pageable: Pageable,
    ): List<Event>

    /**
     * startDt \in [[startDateTimeMin], [startDateTimeMax]]
     * endDt \in [[endDateTimeMin], [endDateTimeMax]]
     * title containing ignore case [titleContainingIgnoreCase]
     * owner NOT [ownerNot]
     * ORDER BY LIMIT [pageable]
     */
    fun findAllByStartDateTimeGreaterThanEqualAndStartDateTimeLessThanEqualAndEndDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndTitleContainingIgnoreCaseAndOwnerNot(
        startDateTimeMin: Instant,
        startDateTimeMax: Instant,
        endDateTimeMin: Instant,
        endDateTimeMax: Instant,
        titleContainingIgnoreCase: String,
        ownerNot: User,
        pageable: Pageable,
    ): List<Event>

    fun deleteAllByIdContainingIgnoreCase(idLike: String)
}
