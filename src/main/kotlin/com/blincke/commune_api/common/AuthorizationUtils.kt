package com.blincke.commune_api.common

import com.blincke.commune_api.models.database.users.CommuneUser
import com.blincke.commune_api.models.domain.users.egress.FindAndSyncFirebaseUserResult
import com.blincke.commune_api.models.firebase.FirebaseUser
import com.blincke.commune_api.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken


/**
 * Takes in a principal and resourceOwner and
 * runs decides if it should run an authenticated function
 * or an unauthenticated function
 * Also handles all the logic of parsing unauthenticated /
 * parsing errors etc. into response entities
 *
 * If you wanted to make this simpler and have less responsibility, it could probably take the
 * signature fun <A, U> ...(... authorizedBody: (user: CommuneUser) -> A, unauthorizedBody: (user: CommuneUser) -> B)
 *
 * Intended usage:
 *
 * userService.runAuthorizedOrElse(
 *      resourceOwnerId,
 *      principal,
 *      authorizedBody = { me ->
 *          doSomethingAuthorized(me)
 *      },
 *      unauthorizedBody = { me ->
 *          doSomethingUnauthorized()
 *      }
 * )
 */
fun UserService.runAuthorizedOrElse(
        resourceOwnerId: String,
        principal: JwtAuthenticationToken,
        authorizedBody: (authorizedUser: CommuneUser) -> ResponseEntity<out Any?>,
        unauthorizedBody: (unauthorizedUser: CommuneUser) -> ResponseEntity<out Any?>,
) = when (val parseResult = jwtToFirebaseUser(principal)) {
    is ParseJWTResult.EmailNotVerified -> ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email Not Verified")
    is ParseJWTResult.Success -> {
        transformToCommuneUserAndRun(parseResult.firebaseUser) {
            if (resourceOwnerId == parseResult.firebaseUser.uid) {
                authorizedBody(it)
            } else {
                unauthorizedBody(it)
            }
        }
    }

    is ParseJWTResult.JwtAttributeMissing -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            TokenMissingAttributesBody(missingAttributes = parseResult.missingAttributes), // TODO - probably don't expose this info
    )
}

fun UserService.runAuthorized(
        resourceOwnerId: String,
        principal: JwtAuthenticationToken,
        body: (unauthorizedUser: CommuneUser) -> ResponseEntity<out Any?>,
) = runAuthorizedOrElse(resourceOwnerId, principal, authorizedBody = body) {
    ResponseEntity.status(HttpStatus.FORBIDDEN).build()
}

/**
 * This function is ugly, but it's private so who cares
 * maybe change in the future to be just a generic transform, but
 * it just simplifies the calling code a bit for now - TODO - think about this more
 */
private fun UserService.transformToCommuneUserAndRun(
        firebaseUser: FirebaseUser,
        body: (user: CommuneUser) -> ResponseEntity<out Any?>
) = when (val userResult = findAndSyncDatabaseWithActiveFirebaseUser(firebaseUser)) {
    is FindAndSyncFirebaseUserResult.UserExistsAndIsUpToDate -> body(userResult.user)
    is FindAndSyncFirebaseUserResult.UserExistsAndWasUpdated -> body(userResult.user)
    is FindAndSyncFirebaseUserResult.CreatedNewUser -> body(userResult.user)
    is FindAndSyncFirebaseUserResult.DatabaseError -> ResponseEntity.internalServerError().build()
}

private sealed interface ParseJWTResult {
    object EmailNotVerified : ParseJWTResult
    data class JwtAttributeMissing(val missingAttributes: List<String>) : ParseJWTResult
    data class Success(val firebaseUser: FirebaseUser) : ParseJWTResult
}

private fun jwtToFirebaseUser(
        principal: JwtAuthenticationToken,
): ParseJWTResult {
    val jwtAttributes = principal.tokenAttributes

    val missingAttributes = mutableListOf<String>()

    val emailVerified = jwtAttributes["email_verified"]?.let { it as Boolean }
    if (emailVerified == null) {
        missingAttributes.add("email")
    }
    val name = jwtAttributes["name"]?.let { it as String }
    if (name == null) {
        missingAttributes.add("name")
    }
    val uid = jwtAttributes["user_id"]?.let { it as String }
    if (uid == null) {
        missingAttributes.add("user_id")
    }
    val email = jwtAttributes["email"]?.let { it as String }
    if (email == null) {
        missingAttributes.add("email")
    }

    if (missingAttributes.size > 0) {
        return ParseJWTResult.JwtAttributeMissing(missingAttributes = missingAttributes)
    }

    if (!emailVerified!!) {
        return ParseJWTResult.EmailNotVerified
    }

    return ParseJWTResult.Success(
            FirebaseUser(
                    uid = uid!!,
                    name = name!!,
                    email = email!!,
            )
    )
}

private data class TokenMissingAttributesBody(
        val message: String = "Invalid JWT",
        val missingAttributes: List<String>,
)