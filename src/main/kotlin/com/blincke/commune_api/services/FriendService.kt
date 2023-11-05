package com.blincke.commune_api.services

import com.blincke.commune_api.models.database.friends.Status
import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.friends.egress.DeleteFriendRequestResult
import com.blincke.commune_api.models.domain.friends.egress.FriendRequestResult
import com.blincke.commune_api.models.domain.users.egress.GetCommuneUserResult
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import org.springframework.stereotype.Service

// TODO - right now these are returning friendships - return users instead
@Service
class FriendService(
        private val userService: UserService,
) {
    // TODO - this looks slow - write a custom query
    fun getMyConfirmedFriends(requester: CommuneUser, page: Int, pageSize: Int) =
            try {
                val requested = requester.requestedFriends
                        .filter { it.status == Status.ACCEPTED }
                        .map { it.friendshipId.recipient }
                val recipients = requester.requestedFriends
                        .filter { it.status == Status.ACCEPTED }
                        .map { it.friendshipId.requester }

                (requested + recipients)
                        .subList(page * pageSize, (page + 1) * pageSize)
                        .map { it.toPublicUser() }
            } catch (e: Exception) {
                listOf()
            }

    fun getSomeoneElsesConfirmedFriends(theirId: String, page: Int, pageSize: Int) =
            when (val result = userService.getDatabaseUserById(theirId)) {
                is GetCommuneUserResult.Active -> getMyConfirmedFriends(result.user, page, pageSize)
                is GetCommuneUserResult.DoesntExist -> listOf()
            }

    fun getFriendRequestsISentThatArePending(requester: CommuneUser, page: Int, pageSize: Int) =
            try {
                requester.requestedFriends
                        .filter { it.status == Status.PENDING }
                        .map { it.friendshipId.recipient }
                        .subList(page * pageSize, (page + 1) * pageSize)
                        .map { it.toPublicUser() }
            } catch (e: Exception) {
                listOf()
            }

    fun getFriendRequestsSentToMeThatArePending(requester: CommuneUser, page: Int, pageSize: Int) =
            try {
                requester.recipientToFriendRequests
                        .filter { it.status == Status.PENDING }
                        .subList(page * pageSize, (page + 1) * pageSize)
                        .map { it.friendshipId.requester }
                        .map { it.toPublicUser() }
            } catch (e: Exception) {
                listOf()
            }

    fun getSuggestedFriendsForMe(requester: CommuneUser, page: Int, pageSize: Int) = try {
        getMyConfirmedFriends(requester, page, pageSize)
    } catch (e: Exception) {
        listOf()
    }

    fun initiateOrTransitionFriend(requester: CommuneUser, recipientId: String): FriendRequestResult =
            when (val result = userService.getPublicUserById(recipientId)) {
                is GetPublicUserResult.Active -> FriendRequestResult.NothingToDo // TODO
                is GetPublicUserResult.DoesntExist -> FriendRequestResult.RecipientDoesntExist
            }

    fun deleteFriendship(requester: CommuneUser, toDeleteId: String): DeleteFriendRequestResult =
            when (userService.getPublicUserById(toDeleteId)) {
                is GetPublicUserResult.Active -> {
                    val doesFriendshipExist = false // TODO
                    if (doesFriendshipExist) {
                        DeleteFriendRequestResult.Succeeded
                    } else {
                        DeleteFriendRequestResult.FriendshipDoesntExist
                    }
                }

                is GetPublicUserResult.DoesntExist -> DeleteFriendRequestResult.RecipientDoesntExist
            }

}