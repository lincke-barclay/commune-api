package com.blincke.commune_api.repositories

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.FriendshipId
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<Friendship, FriendshipId>
