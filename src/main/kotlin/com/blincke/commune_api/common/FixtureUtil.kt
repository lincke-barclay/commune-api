package com.blincke.commune_api.common

import java.time.Instant
import kotlin.random.Random

fun generateRandomName(): String = names.random()

val capitals = ('A'..'Z').toList()
val lowerCase = ('a'..'z').toList()
val nums = ('0'..'9').toList()

fun generateRandomString(
    length: Int,
    allowedChars: List<Char> = capitals + lowerCase + nums
): String {
    return (1..length)
        .map { allowedChars.random() }
        .joinToString("")
}

fun generateRandomUpperCaseWord(length: Int): String {
    return generateRandomString(
        1,
        capitals
    ) + generateRandomString(
        length - 1,
        lowerCase
    )
}

fun generateRandomWord(length: Int): String {
    return generateRandomString(length, lowerCase)
}

fun generateRandomSentence(words: Int): String {
    return generateRandomUpperCaseWord(
        Random.nextInt(
            1,
            10
        )
    ) + (1..words).joinToString(separator = " ") {
        generateRandomWord(Random.nextInt(1, 10))
    }
}

fun generateRandomEmail() = generateRandomString(5) + "@" + generateRandomString(4) + ".com"


fun randomInstantBetween(startInclusive: Instant, endExclusive: Instant): Instant {
    return Instant.ofEpochSecond(
        Random.nextLong(
            startInclusive.epochSecond,
            endExclusive.epochSecond
        )
    )
}
