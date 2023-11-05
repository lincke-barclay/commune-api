package com.blincke.commune_api.services

import com.blincke.commune_api.models.database.friends.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import org.springframework.stereotype.Service

// TODO - right now these are returning friendships - return users instead
@Service
class FriendService(
        private val userService: UserService,
) {
    // TODO - this looks slow - write a custom query
    fun getConfirmedFriends(user: User, page: Int, pageSize: Int): List<User> {
        val requested = user.requestedFriends
                .filter { it.status == Status.ACCEPTED }
                .map { it.friendshipId.recipient }
        val recipients = user.requestedFriends
                .filter { it.status == Status.ACCEPTED }
                .map { it.friendshipId.requester }

        return (requested + recipients)
                .subList(page * pageSize, (page + 1) * pageSize)
    }

    fun getConfirmedFriendsById(userId: String, page: Int, pageSize: Int) =
            when (val result = userService.getUserById(userId)) {
                is GetUserResult.Active -> getConfirmedFriends(result.user, page, pageSize)
                is GetUserResult.DoesntExist -> listOf()
            }

    fun getFriendRequestsUserSentThatArePending(requester: User, page: Int, pageSize: Int) =
            requester.requestedFriends
                    .filter { it.status == Status.PENDING }
                    .map { it.friendshipId.recipient }
                    .subList(page * pageSize, (page + 1) * pageSize)

    fun getFriendRequestsSentToUserThatArePending(requester: User, page: Int, pageSize: Int) =
            requester.recipientToFriendRequests
                    .filter { it.status == Status.PENDING }
                    .subList(page * pageSize, (page + 1) * pageSize)
                    .map { it.friendshipId.requester }

    fun getSuggestedFriendsForUser(requester: User, page: Int, pageSize: Int) =
            getConfirmedFriends(requester, page, pageSize)

    fun initiateOrTransitionFriend(requester: User, recipientId: String): FriendRequestResult =
            when (userService.getUserById(recipientId)) {
                is GetUserResult.Active -> FriendRequestResult.NothingToDo // TODO
                is GetUserResult.DoesntExist -> FriendRequestResult.RecipientDoesntExist
            }

    fun deleteFriendship(requester: User, toDeleteId: String): DeleteFriendRequestResult =
            when (userService.getUserById(toDeleteId)) {
                is GetUserResult.Active -> {
                    val doesFriendshipExist = false // TODO
                    if (doesFriendshipExist) {
                        DeleteFriendRequestResult.Succeeded
                    } else {
                        DeleteFriendRequestResult.FriendshipDoesntExist
                    }
                }

                is GetUserResult.DoesntExist -> DeleteFriendRequestResult.RecipientDoesntExist
            }

}