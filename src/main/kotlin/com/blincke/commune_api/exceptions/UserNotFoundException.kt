package com.blincke.commune_api.exceptions

class UserNotFoundException(
        override val message: String? = "",
) : RuntimeException(message)