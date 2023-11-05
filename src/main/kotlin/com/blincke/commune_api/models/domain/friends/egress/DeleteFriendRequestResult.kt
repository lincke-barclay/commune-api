package com.blincke.commune_api.models.domain.friends.egress

sealed interface DeleteFriendRequestResult {
    data object Succeeded : DeleteFriendRequestResult
    data object FriendshipDoesntExist : DeleteFriendRequestResult
    data object RecipientDoesntExist : DeleteFriendRequestResult
}
