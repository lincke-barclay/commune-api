package com.blincke.commune_api.controllers

import com.blincke.commune_api.common.*
import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.FriendshipId
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.repositories.EventRepository
import com.blincke.commune_api.repositories.FriendshipRepository
import com.blincke.commune_api.repositories.UserRepository
import com.blincke.commune_api.services.models.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.random.Random

@RestController
@RequestMapping("/fixtures")
class FixtureController(
    private val friendshipRepository: FriendshipRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
) {
    private fun saveNRandomUsers(n: Int): List<User> {
        return (0 until n).map {
            userRepository.save(
                User(
                    firebaseId = "Fixture|$it",
                    name = generateRandomName(),
                    email = generateRandomEmail(),
                    profilePictureUrl = "https://picsum.photos/200",
                )
            )
        }
    }

    private fun makeNRandomEventsForMe(n: Int, myUser: User) {
        (0 until n).forEach {
            val startDateTime = randomInstantBetween(
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.DAYS)
            )
            val endDateTime = randomInstantBetween(
                startDateTime.plus(1, ChronoUnit.SECONDS),
                Instant.now().plus(10, ChronoUnit.DAYS),
            )

            eventRepository.save(
                Event(
                    id = "Fixture|MyEvent|$it",
                    title = generateRandomWord(5),
                    description = generateRandomSentence(90),
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    owner = myUser,
                )
            )
        }
    }

    private fun generateNRandomFriendships(n: Int, users: List<User>) {
        (0 until n).forEach { _ ->
            val user1 = users.random()
            val user2 = users.random()
            if (user1 != user2) {
                friendshipRepository.save(
                    Friendship(
                        friendshipId = FriendshipId(
                            requester = user1,
                            recipient = user2,
                        ),
                    )
                )
            }
        }
    }

    private fun generateNRandomEventsForRandomUsers(n: Int, users: List<User>) {
        // Insert 100 random events for other users
        (0 until n).forEach {
            val startDateTime = randomInstantBetween(
                Instant.now(),
                Instant.now().plus(10, ChronoUnit.DAYS)
            )
            val endDateTime = randomInstantBetween(
                startDateTime.plus(1, ChronoUnit.SECONDS),
                Instant.now().plus(10, ChronoUnit.DAYS),
            )
            eventRepository.save(
                Event(
                    id = "Fixture|$it",
                    title = generateRandomWord(5),
                    description = generateRandomSentence(90),
                    startDateTime = startDateTime,
                    endDateTime = endDateTime,
                    owner = users.random(),
                )
            )
        }
    }

    private fun giveMeNRandomFriends(n: Int, myUser: User, users: List<User>) {
        (0 until n).forEach { _ ->
            val user1 = if (Random.nextInt(2) == 0) myUser else users.random()
            val user2 = if (user1 == myUser) users.random() else myUser
            friendshipRepository.save(
                Friendship(
                    friendshipId = FriendshipId(
                        requester = user1,
                        recipient = user2,
                    ),
                )
            )
        }
    }

    @PostMapping("/randomData")
    fun seedEvents(
        principal: JwtAuthenticationToken,
    ) = userService.runAuthorized("lGvyfC6r6sUEY794p5neKUH3B0t2", principal) { myUser ->
        // Insert 1000 random users
        val users = saveNRandomUsers(1000)
        makeNRandomEventsForMe(50, myUser)
        generateNRandomEventsForRandomUsers(100, users)
        generateNRandomFriendships(500, users)
        giveMeNRandomFriends(20, myUser, users)

        ResponseEntity.ok().build()
    }

    @DeleteMapping("/randomData")
    @Transactional
    fun deleteRandomData(
        principal: JwtAuthenticationToken,
    ) = userService.runAuthorized("lGvyfC6r6sUEY794p5neKUH3B0t2", principal) { myUser ->
        eventRepository.deleteAllByIdContainingIgnoreCase("Fixture|")
        userRepository.deleteAllByFirebaseIdContainingIgnoreCase("Fixture|")
        ResponseEntity.noContent().build()
    }
}
