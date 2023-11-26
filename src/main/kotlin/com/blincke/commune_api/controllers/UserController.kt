package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthenticated
import com.blincke.commune_api.common.runAuthorized
import com.blincke.commune_api.common.runAuthorizedOrElse
import com.blincke.commune_api.logging.AppLoggerFactory
import com.blincke.commune_api.models.domain.users.egress.GetUserResult
import com.blincke.commune_api.models.network.users.egress.toPrivateUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto
import com.blincke.commune_api.services.images.ProfilePictureService
import com.blincke.commune_api.services.models.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(
    private val userService: UserService,
    private val profilePictureService: ProfilePictureService,
) {
    private val logger = AppLoggerFactory.getLogger(this)

    @GetMapping("/users/{userId}")
    fun getUser(
        @PathVariable("userId") userId: String,
        principal: JwtAuthenticationToken,
    ) = userService.runAuthorizedOrElse(
        userId,
        principal,
        authorizedBody = { me ->
            AppLoggerFactory.getLogger(this).debug("Request to get private user for user with id $userId")
            ResponseEntity.ok(me.toPrivateUserResponseDto())
        },
        unauthorizedBody = { _ ->
            AppLoggerFactory.getLogger(this).debug("Request to get public user for user with id $userId")
            when (val notMeResult = userService.getUserById(userId)) {
                is GetUserResult.Active -> ResponseEntity.ok(notMeResult.user.toPublicUserResponseDto())
                is GetUserResult.DoesntExist -> ResponseEntity.notFound().build()
            }
        }
    )

    @PostMapping("/users/{userId}/profile-picture")
    fun uploadProfilePicture(
        @RequestPart(value = "file") file: MultipartFile,
        @PathVariable("userId") userId: String,
        principal: JwtAuthenticationToken,
    ): ResponseEntity<out Any> {
        return userService.runAuthorized(userId, principal) { user ->
            val url = this.profilePictureService.saveProfilePhotoFor(file, user)
            userService.updateProfilePictureUrl(user, url)
            ResponseEntity.created(url.toURI()).build()
        }
    }

    @GetMapping("/users")
    fun getPublicUsersByQuery(
        principal: JwtAuthenticationToken,
        @RequestParam("page", required = true) page: Int,
        @RequestParam("pageSize", required = true) pageSize: Int,
        @RequestParam("queryStr", required = true) queryStr: String,
    ): ResponseEntity<out Any> {
        logger.debug("Request to get users with query: $queryStr")
        return userService.runAuthenticated(principal) { requester ->
            logger.debug("From user with id: ${requester.firebaseId}")
            ResponseEntity.ok(
                userService.getUsersByQuery(
                    requester,
                    queryStr,
                    page,
                    pageSize,
                ).map { it.toPublicUserResponseDto() }
            )
        }
    }
}
