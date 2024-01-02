package util

import com.blincke.commune_api.models.database.events.Event
import com.blincke.commune_api.models.database.invitations.Invitation
import com.blincke.commune_api.models.database.invitations.Status
import com.blincke.commune_api.models.database.users.User
import com.blincke.commune_api.repositories.EventRepository
import com.blincke.commune_api.repositories.InvitationRepository
import com.blincke.commune_api.repositories.UserRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DataBootstrapUtility(
    private val invitationRepository: InvitationRepository,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
) {
    // FIXME: should all the repositories be SpyBeans?

    private final val defaultEmail = "a@a.com"
    private final val defaultFirebaseId = "123"
    private final val defaultName = "Test"
    private final val defaultEventTitle = "Test"

    private final val defaultUser =
        userRepository.save(User(email = defaultEmail, firebaseId = defaultFirebaseId, name = defaultName))
    private final val defaultEvent = eventRepository.save(
        Event(
            owner = defaultUser,
            title = defaultEventTitle,
            startDateTime = Instant.now(),
            endDateTime = Instant.now(),
            description = "",
        )
    )

    fun createAndSaveUser(email: String, firebaseId: String, name: String): User {
        return userRepository.save(
            User(email = email, firebaseId = firebaseId, name = name),
        )
    }

    fun createAndSaveEvent(
        owner: User = defaultUser,
        title: String = defaultEventTitle,
        startDateTime: Instant = Instant.now(),
        endDateTime: Instant = Instant.now(),
        longDescription: String = "",
        shortDescription: String = "",
    ): Event {
        return eventRepository.save(
            Event(
                owner = owner,
                title = title,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                description = shortDescription,
            )
        )
    }

    fun createAndSaveInvitation(
        event: Event = defaultEvent,
        recipient: User = defaultUser,
        sender: User = defaultUser,
        status: Status = Status.PENDING,
    ): Invitation {
        return invitationRepository.save(
            Invitation(
                event = event,
                recipient = recipient,
                sender = sender,
                status = status,
                expirationTimestamp = event.startDateTime,
            )
        )
    }
}