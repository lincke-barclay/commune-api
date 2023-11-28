package com.blincke.commune_api.services.models

import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.models.domain.users.egress.FindAndSyncFirebaseUserResult
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.models.firebase.FirebaseUser
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.net.URL

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findAndSyncDatabaseWithActiveFirebaseUser(user: FirebaseUser) =
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
                    user.toNewCommuneUser()
                )
            )
        }

    fun getUserById(id: String) =
        userRepository.findByIdOrNull(id)?.let {
            GetUserResult.Active(it)
        } ?: GetUserResult.DoesntExist

    fun updateProfilePictureUrl(user: User, url: URL) {
        userRepository.save(user.copy(profilePictureUrl = url.toString()))
    }

    fun getUsersByQuery(requester: User, queryString: String, page: Int, pageSize: Int) =
        userRepository.getAllByFirebaseIdNotAndNameContainingIgnoreCase(
            requester.firebaseId,
            queryString.lowercase(),
            Pageable.ofSize(pageSize).withPage(page)
        )
}
