package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import java.time.Instant
import java.util.Date

interface InvitationRepository : JpaRepository<Invitation, String> {
    fun findAllByRecipient(recipient: User, pageable: Pageable): List<Invitation>

    fun findAllByStatusAndExpirationTimestampBeforeAndRecipientIs(
        status: Status,
        expirationTimestamp: Instant,
        recipient: User,
        pageable: Pageable,
    ): List<Invitation>
}