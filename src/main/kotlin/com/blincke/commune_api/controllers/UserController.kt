package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.runAuthorizedOrElse
import com.blincke.commune_api.models.domain.users.egress.GetPublicUserResult
import com.blincke.commune_api.models.network.users.egress.toPrivateUserResponseDto
import com.blincke.commune_api.models.network.users.egress.toPublicUserResponseDto
import com.blincke.commune_api.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class UserController(
        private val userService: UserService,
) {
    @GetMapping("/users/{userId}")
    fun getUser(
            @PathVariable("userId") userId: String,
            principal: JwtAuthenticationToken,
    ) = userService.runAuthorizedOrElse(
            userId,
            principal,
            authorizedBody = { me ->
                ResponseEntity.ok(me.toPrivateUser().toPrivateUserResponseDto())
            },
            unauthorizedBody = { _ ->
                when (val notMeResult = userService.getPublicUserById(userId)) {
                    is GetPublicUserResult.Active -> ResponseEntity.ok(notMeResult.user.toPublicUserResponseDto())
                    is GetPublicUserResult.DoesntExist -> ResponseEntity.notFound().build()
                }
            }
    )
}
