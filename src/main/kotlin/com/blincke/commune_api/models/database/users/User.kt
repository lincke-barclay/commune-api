package com.blincke.commune_api.models.database.users

import com.blincke.commune_api.models.database.friends.Friendship
import com.blincke.commune_api.models.database.friends.Status
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "commune_user")
class User(
    @Id
    @Column(name = "firebase_id")
    val firebaseId: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "profile_picture_url")
    val profilePictureUrl: String? = null,

    @OneToMany(mappedBy = "friendshipId.requester", cascade = [CascadeType.ALL], orphanRemoval = true)
    val friendRequestsISent: MutableSet<Friendship>,

    @OneToMany(mappedBy = "friendshipId.recipient", cascade = [CascadeType.ALL], orphanRemoval = true)
    val friendRequestsSentToMe: MutableSet<Friendship>
) {
    // TODO - I think queries would be faster - but that'd be a premature optimization
    // TODO Experiment
    val usersWhoSentAFriendRequestToMeThatIsPending
        get() = friendRequestsSentToMe
            .filter { it.status == Status.PENDING }
            .map { it.friendshipId.requester }
    private val usersWhoSentAFriendRequestToMeThatIsAccepted
        get() = friendRequestsSentToMe
            .filter { it.status == Status.ACCEPTED }
            .map { it.friendshipId.requester }

    val usersWhoISentAFriendRequestToThatIsPending
        get() = friendRequestsISent
            .filter { it.status == Status.PENDING }
            .map { it.friendshipId.recipient }

    private val usersWhoISentAFriendRequestToThatIsAccepted
        get() = friendRequestsISent
            .filter { it.status == Status.ACCEPTED }
            .map { it.friendshipId.recipient }

    // This is an exact copy of one of the methods in userRepository -
    // TODO - how the hell does JPQL work and what's best practice?
    val activeFriends
        get() = usersWhoISentAFriendRequestToThatIsAccepted +
                usersWhoSentAFriendRequestToMeThatIsAccepted

    fun copy(
        firebaseId: String? = null,
        name: String? = null,
        email: String? = null,
        requestedFriends: MutableSet<Friendship>? = null,
        recipientToFriendRequests: MutableSet<Friendship>? = null,
        profilePictureUrl: String? = null,
    ) = User(
        firebaseId = firebaseId ?: this.firebaseId,
        name = name ?: this.name,
        email = email ?: this.email,
        friendRequestsISent = requestedFriends ?: this.friendRequestsISent,
        friendRequestsSentToMe = recipientToFriendRequests ?: this.friendRequestsSentToMe,
        profilePictureUrl = profilePictureUrl ?: this.profilePictureUrl,
    )
}