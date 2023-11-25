package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.FriendshipId
import com.blincke.commune_api.models.database.users.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FriendshipRepository : JpaRepository<Friendship, FriendshipId> {
    @Query(
        value = "select f from Friendship f where " +
                "(f.friendshipId.requester = (?1) and f.friendshipId.recipient = (?2))" +
                "or " +
                "(f.friendshipId.recipient = (?1) and f.friendshipId.requester = (?2))"
    )
    fun selectRelevantFriendships(user1: User, user2: User): List<Friendship>
}
