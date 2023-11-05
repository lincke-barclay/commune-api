package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.database.users.User

sealed class FindAndSyncFirebaseUserResult {
    data class CreatedNewUser(val user: User) : FindAndSyncFirebaseUserResult()
    data class UserExistsAndIsUpToDate(val user: User) : FindAndSyncFirebaseUserResult()
    data class UserExistsAndWasUpdated(val user: User) : FindAndSyncFirebaseUserResult()
}