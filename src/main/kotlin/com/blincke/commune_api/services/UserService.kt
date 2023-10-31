package com.blincke.commune_api.services

import com.blincke.commune_api.models.domain.users.egress.CreateUserResult
import com.blincke.commune_api.models.domain.users.egress.GetCommuneUserResult
import com.blincke.commune_api.models.domain.users.egress.GetPrivateUserResult
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import com.blincke.commune_api.models.network.users.ingress.POSTUserRequestDto
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserService(
        private val userRepository: UserRepository,
) {

    fun createUser(createUserRequest: POSTUserRequestDto): CreateUserResult =
            when (val potentialConflict = getPublicUserByEmail(createUserRequest.email)) {
                is GetPublicUserResult.Active -> CreateUserResult.Conflict(potentialConflict.user)
                is GetPublicUserResult.DoesntExist -> try {
                    CreateUserResult.Created(userRepository.save(createUserRequest.toNewCommuneUser()).toPublicUser())
                } catch (e: Exception) {
                    CreateUserResult.UnknownFailure
                }
            }

    fun getPublicUserByEmail(userEmail: String) =
            userRepository.findFirstByEmail(userEmail)?.let {
                GetPublicUserResult.Active(it.toPublicUser())
            } ?: GetPublicUserResult.DoesntExist

    fun getPrivateUserById(id: String) =
            userRepository.findByIdOrNull(id)?.let {
                GetPrivateUserResult.Active(it.toPrivateUser())
            } ?: GetPrivateUserResult.DoesntExist

    fun getPublicUserById(id: String) =
            userRepository.findByIdOrNull(id)?.let {
                GetPublicUserResult.Active(it.toPublicUser())
            } ?: GetPublicUserResult.DoesntExist

    fun getDatabaseUserById(id: String) =
            userRepository.findByIdOrNull(id)?.let {
                GetCommuneUserResult.Active(it)
            } ?: GetCommuneUserResult.DoesntExist
}
