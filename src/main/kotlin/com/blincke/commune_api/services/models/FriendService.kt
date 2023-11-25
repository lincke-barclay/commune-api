package com.blincke.commune_api.services.models

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.FriendshipId
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.friends.FriendshipState
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.repositories.FriendshipRepository
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FriendService(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val friendshipRepository: FriendshipRepository,
) {
    fun getConfirmedFriends(user: User, page: Int, pageSize: Int): List<User> {
        return userRepository.selectConfirmedFriends(user, Pageable.ofSize(pageSize).withPage(page))
    }

    fun getConfirmedFriendsById(userId: String, page: Int, pageSize: Int) =
        when (val result = userService.getUserById(userId)) {
            is GetUserResult.Active -> getConfirmedFriends(result.user, page, pageSize)
            is GetUserResult.DoesntExist -> listOf()
        }

    fun getFriendRequestsUserSentThatArePending(requester: User, page: Int, pageSize: Int) =
        userRepository.selectFriendshipsThatArePendingThatISent(requester, Pageable.ofSize(pageSize).withPage(page))

    fun getFriendRequestsSentToUserThatArePending(requester: User, page: Int, pageSize: Int) =
        userRepository.selectFriendshipsThatArePendingThatWhereSentToMe(
            requester,
            Pageable.ofSize(pageSize).withPage(page)
        )

    fun getSuggestedFriendsForUser(requester: User, queryString: String, page: Int, pageSize: Int) =
        userRepository.selectSuggestedFriends(
            requester,
            queryString.lowercase(),
            Pageable.ofSize(pageSize).withPage(page)
        )

    @Transactional
    fun initiateOrTransitionFriend(requester: User, recipientId: String): FriendRequestResult {
        if (requester.firebaseId == recipientId) {
            return FriendRequestResult.RequestedToSameUser
        }

        return when (val result = userService.getUserById(recipientId)) {
            is GetUserResult.Active -> {
                createFriendship(requester, result.user)
            }

            is GetUserResult.DoesntExist -> FriendRequestResult.RecipientDoesntExist
        }
    }

    fun getFriendshipState(
        user1: User,
        user2: User,
    ): FriendshipState {
        val relevantFriendships = friendshipRepository.selectRelevantFriendships(user1, user2)

        if (relevantFriendships.size > 2) {
            throw Exception("Relevant friendships is greater than 2 - this is a bug")
        }

        return FriendshipState(
            user1 = user1,
            user1Initiated = user1 in relevantFriendships.map { it.friendshipId.requester },
            user2 = user2,
            user2Initiated = user2 in relevantFriendships.map { it.friendshipId.requester }
        )
    }

    @Transactional
    fun createFriendship(
        requester: User,
        recipient: User
    ): FriendRequestResult {
        assert(requester.firebaseId != recipient.firebaseId) {
            "Tried to transition friendship for same user with id " +
                    "${recipient.firebaseId}. This logic should be accounted for above"
        }
        val state = getFriendshipState(
            user1 = requester,
            user2 = recipient,
        )
        val friendship = Friendship(
            friendshipId = FriendshipId(
                requester = requester,
                recipient = recipient
            ),
        )
        return if (state.user1Initiated) {
            FriendRequestResult.NothingToDo
        } else if (state.user2Initiated) {
            // user1Initiated is false
            friendshipRepository.save(friendship)
            FriendRequestResult.Accepted
        } else {
            friendshipRepository.save(friendship)
            FriendRequestResult.Created
        }
    }

    fun deleteFriendship(user1: User, toDeleteId: String): DeleteFriendRequestResult {
        when (val result = userService.getUserById(toDeleteId)) {
            is GetUserResult.Active -> {
                var friendship = friendshipRepository.findByIdOrNull(FriendshipId(user1, result.user))
                if (friendship == null) {
                    friendship = friendshipRepository.findByIdOrNull(FriendshipId(result.user, user1))
                }
                return if (friendship == null) {
                    DeleteFriendRequestResult.FriendshipDoesntExist
                } else {
                    friendshipRepository.delete(friendship)
                    DeleteFriendRequestResult.Succeeded
                }
            }

            GetUserResult.DoesntExist -> return DeleteFriendRequestResult.RecipientDoesntExist
        }
    }
}