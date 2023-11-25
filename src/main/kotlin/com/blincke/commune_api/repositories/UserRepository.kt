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
                "and u != (?1) " +
                "and lower(u.name) like %?2%"
    )
    fun selectSuggestedFriends(
        user: User,
        nameContainsLower: String,
        pageable: Pageable,
    ): List<User>

    /**
     * select f1.recipient
     * from friendship f1
     * inner join friendship f2
     * on f1.requester = f2.recipient
     * and f1.recipient = f2.requester
     * and f1.requester = 'userId';
     */
    @Query(
        value = "select f1.friendshipId.recipient " +
                "from Friendship f1 " +
                "inner join Friendship f2 " +
                "on f1.friendshipId.recipient = f2.friendshipId.requester " +
                "and f2.friendshipId.recipient = f1.friendshipId.requester " +
                "and f1.friendshipId.requester = (?1)"
    )
    fun selectConfirmedFriends(user: User, pageable: Pageable): List<User>

    /**
     *  select f1.recipient
     *  from friendship f1
     *  where f1.requester = 'a'
     *  and 'a' not in
     *  (select f2.recipient
     *  from friendship f2
     *  where f2.requester=f1.recipient);
     */
    @Query(
        value = "select f1.friendshipId.recipient " +
                "from Friendship f1 " +
                "where f1.friendshipId.requester = (?1) " +
                "and (?1) not in " +
                "(select f2.friendshipId.recipient " +
                "from Friendship f2 " +
                "where f2.friendshipId.requester = f1.friendshipId.recipient)"
    )
    fun selectFriendshipsThatArePendingThatISent(user: User, pageable: Pageable): List<User>


    /**
     *  select f1.requester
     *  from friendship f1
     *  where f1.recipient = 'a'
     *  and 'a' not in
     *  (select f2.requester
     *  from friendship f2
     *  where f2.recipient=f1.requester);
     */
    @Query(
        value = "select f1.friendshipId.requester " +
                "from Friendship f1 " +
                "where f1.friendshipId.recipient = (?1) " +
                "and (?1) not in " +
                "(select f2.friendshipId.requester " +
                "from Friendship f2 " +
                "where f2.friendshipId.recipient = f1.friendshipId.requester)"
    )
    fun selectFriendshipsThatArePendingThatWhereSentToMe(user: User, pageable: Pageable): List<User>
}
