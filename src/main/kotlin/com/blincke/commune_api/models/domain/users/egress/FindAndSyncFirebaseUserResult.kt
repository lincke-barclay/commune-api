package com.blincke.commune_api.models.domain.users.egress

import com.blincke.commune_api.models.database.users.CommuneUser

sealed class FindAndSyncFirebaseUserResult {
    data class CreatedNewUser(val user: CommuneUser) : FindAndSyncFirebaseUserResult()
    data class UserExistsAndIsUpToDate(val user: CommuneUser) : FindAndSyncFirebaseUserResult()
    data class UserExistsAndWasUpdated(val user: CommuneUser) : FindAndSyncFirebaseUserResult()
    object DatabaseError : FindAndSyncFirebaseUserResult()
}