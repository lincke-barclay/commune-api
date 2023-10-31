package com.blincke.commune_api.models.domain.friends.egress

sealed interface FriendRequestResult {
    // A new friendship was created
    object Created : FriendRequestResult

    // An existing friend request was accepted
    object Accepted : FriendRequestResult

    // You've already made a friend request to this user and nothing changed
    object NothingToDo : FriendRequestResult

    object RecipientDoesntExist : FriendRequestResult
}
