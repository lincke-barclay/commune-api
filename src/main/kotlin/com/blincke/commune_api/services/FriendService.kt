package com.blincke.commune_api.services

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.FriendshipId
import com.blincke.commune_api.models.database.friends.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.repositories.FriendshipRepository
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

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

    fun getSuggestedFriendsForUser(requester: User, page: Int, pageSize: Int) =
        userRepository.selectSuggestedFriends(requester, Pageable.ofSize(pageSize).withPage(page))

    fun initiateOrTransitionFriend(requester: User, recipientId: String): FriendRequestResult {
        if (requester.firebaseId == recipientId) {
            return FriendRequestResult.RequestedToSameUser
        }

        return when (val result = userService.getUserById(recipientId)) {
            is GetUserResult.Active -> {
                when (result.user) {
                    in requester.activeFriends,
                    in requester.usersWhoISentAFriendRequestToThatIsPending -> {
                        FriendRequestResult.NothingToDo
                    }

                    in requester.usersWhoSentAFriendRequestToMeThatIsPending -> {
                        transitionPendingFriendship(result.user, requester)
                        FriendRequestResult.Accepted
                    }

                    else -> {
                        createNewFriendRequest(requester, result.user)
                        FriendRequestResult.Created
                    }
                }
            }

            is GetUserResult.DoesntExist -> FriendRequestResult.RecipientDoesntExist
        }
    }


    private fun createNewFriendRequest(requester: User, recipient: User) {
        assert(requester.firebaseId != recipient.firebaseId) {
            "Tried to transition friendship for same user with id " +
                    "${recipient.firebaseId}. This logic should be accounted for above"
        }

        val friendship = Friendship(
            friendshipId = FriendshipId(
                requester = requester,
                recipient = recipient
            ),
            status = Status.PENDING,
        )

        friendshipRepository.save(friendship)
    }

    private fun transitionPendingFriendship(user1: User, user2: User) {
        assert(user1.firebaseId != user2.firebaseId) { "Cannot transition a friendship for the same user with id: ${user1.firebaseId}" }

        // case 1: user1 is the requester
        val case1 = user1.friendRequestsISent
            .filter { it.friendshipId.recipient.firebaseId == user2.firebaseId }

        // case 2: user2 is the requester
        val case2 = user2.friendRequestsISent
            .filter { it.friendshipId.recipient.firebaseId == user2.firebaseId }

        val filtered = case1 + case2

        assert(filtered.size == 1) {
            "Trying to transition friendship for user ${user1.firebaseId} and ${user2.firebaseId} but for some reason" +
                    "there are multiple friend requests that exist from this user - this should not be the case because request + recipient is " +
                    "a unique compound key - and this is definitely a bug"
        }

        val request = filtered.first()

        assert(request.status == Status.PENDING) {
            "Trying to transition friendship for user ${user1.firebaseId} and ${user2.firebaseId} but " +
                    "friendship is accepted - please check your logic in the calling function"
        }

        request.status = Status.ACCEPTED
        friendshipRepository.save(request)
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