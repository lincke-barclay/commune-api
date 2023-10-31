package com.blincke.commune_api.models.domain.friends.egress

sealed interface DeleteFriendRequestResult {
    object Succeeded : DeleteFriendRequestResult
    object FriendshipDoesntExist : DeleteFriendRequestResult
    object RecipientDoesntExist : DeleteFriendRequestResult
}
