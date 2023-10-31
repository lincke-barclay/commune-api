package com.blincke.commune_api.common

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun <T> runAuthorized(
        resourceOwnerId: String,
        principalId: String,
        body: () -> ResponseEntity<T>,
): ResponseEntity<T> = if (resourceOwnerId != principalId) {
    ResponseEntity.status(HttpStatus.FORBIDDEN).build()
} else {
    body()
}
