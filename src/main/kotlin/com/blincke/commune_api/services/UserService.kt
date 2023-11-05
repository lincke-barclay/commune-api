package com.blincke.commune_api.services

import com.blincke.commune_api.models.domain.users.egress.FindAndSyncFirebaseUserResult
import com.blincke.commune_api.models.domain.users.egress.GetCommuneUserResult
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import com.blincke.commune_api.models.firebase.FirebaseUser
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository,
) {
    fun findAndSyncDatabaseWithActiveFirebaseUser(user: FirebaseUser) =
            // For now, just handle all database error cases the same - might fine tune in the future
            try {
                userRepository.findByIdOrNull(user.uid)?.let { communeUser ->
                    if (user.isInSyncWithCommuneUser(communeUser)) {
                        FindAndSyncFirebaseUserResult.UserExistsAndIsUpToDate(
                                user = communeUser,
                        )
                    } else {
                        FindAndSyncFirebaseUserResult.UserExistsAndWasUpdated(
                                user = userRepository.save(
                                        user.mergeWithCommuneUser(communeUser)
                                ),
                        )
                    }
                } ?: run {
                    FindAndSyncFirebaseUserResult.CreatedNewUser(
                            user = userRepository.save(
                                    user.toCreateUserRequest().toCommuneUser()
                            )
                    )
                }
            } catch (e: Exception) {
                FindAndSyncFirebaseUserResult.DatabaseError
            }

    fun getPublicUserById(id: String) =
            userRepository.findByIdOrNull(id)?.let {
                GetPublicUserResult.Active(it.toPublicUser())
            } ?: GetPublicUserResult.DoesntExist

    fun getDatabaseUserById(id: String) =
            userRepository.findByIdOrNull(id)?.let {
                GetCommuneUserResult.Active(it)
            } ?: GetCommuneUserResult.DoesntExist
}
