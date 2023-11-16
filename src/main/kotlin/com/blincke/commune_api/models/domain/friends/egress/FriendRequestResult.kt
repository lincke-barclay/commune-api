package com.blincke.commune_api.models.domain.friends.egress

sealed interface FriendRequestResult {
    // A new friendship was created
    data object Created : FriendRequestResult

    // An existing friend request was accepted
    data object Accepted : FriendRequestResult

    // You've already made a friend request to this user and nothing changed
    data object NothingToDo : FriendRequestResult

    data object RecipientDoesntExist : FriendRequestResult
    data object RequestedToSameUser : FriendRequestResult
}
