package com.blincke.commune_api.models.domain.friends

import com.blincke.commune_api.models.database.users.User

data class FriendshipState(
    val user1: User,
    val user1Initiated: Boolean,
    val user2: User,
    val user2Initiated: Boolean,
)
