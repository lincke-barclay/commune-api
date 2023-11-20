package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.users.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {

    @Query(
        value = "select u from User u " +
                "where u not in " +
                "(select f.friendshipId.requester from Friendship f " +
                "where f.friendshipId.recipient = (?1))" +
                "and u not in " +
                "(select f.friendshipId.recipient from Friendship f " +
                "where f.friendshipId.requester = (?1))" +
                "and u != (?1)"
    )
    fun selectSuggestedFriends(user: User, pageable: Pageable): List<User>

    @Query(
        value = "select u from User u " +
                "where u in " +
                "(select f.friendshipId.requester from Friendship f " +
                "where f.friendshipId.recipient = (?1) and f.status = 'ACCEPTED')" +
                "or u in " +
                "(select f.friendshipId.recipient from Friendship f " +
                "where f.friendshipId.requester = (?1) and f.status = 'ACCEPTED')" +
                "and u != (?1)"
    )
    fun selectConfirmedFriends(user: User, pageable: Pageable): List<User>

    @Query(
        value = "select u from User u " +
                "where u in " +
                "(select f.friendshipId.recipient from Friendship f " +
                "where f.friendshipId.requester = (?1) and f.status = 'PENDING')" +
                "and u != (?1)"
    )
    fun selectFriendshipsThatArePendingThatISent(user: User, pageable: Pageable): List<User>

    @Query(
        value = "select u from User u " +
                "where u in " +
                "(select f.friendshipId.requester from Friendship f " +
                "where f.friendshipId.recipient = (?1) and f.status = 'PENDING')" +
                "and u != (?1)"
    )
    fun selectFriendshipsThatArePendingThatWhereSentToMe(user: User, pageable: Pageable): List<User>
}
