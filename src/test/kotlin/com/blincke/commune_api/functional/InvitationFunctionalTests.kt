package com.blincke.commune_api.com.blincke.commune_api.functional

import com.blincke.commune_api.CommuneApiApplication
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.blincke.commune_api.com.blincke.commune_api.util.TestBase

@SpringBootTest(classes = [CommuneApiApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
class InvitationFunctionalTests : TestBase() {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val senderUserId = "senderUserId"
    private val recipientUserId = "recipientUserId"
    private val eventId = "eventId"

    @Test
    fun `cannot create an event if user is unauthorized`() {
    }

    // Create invitation
    @Test
    fun `creates an invitation`() {
        // Post an invitation for an event to a user

        // Confirm that the response corresponds to the correct event and user
    }

    // Get an invitation for an event

    @Test
    fun `gets an invitation for an event`() {
        // Create an invitation for an event

        // Make a request for the event

        // Confirm that the invitation belongs to the user and corresponds to the correct event
    }

    // Get all invitations for user
    @Test
    fun `gets all invitations for a user`() {
        // Create two invitations for a user

        // Request all invitations for a user

        // Confirm that two invitations are retrieved and they correspond to the correct events for the requesting user
    }

    @Nested
    inner class InvitationPatch {
        @Test
        fun `cannot change invitation ID`() {
            // Create an invitation
            val invitation = dataBootstrapUtility.createAndSaveInvitation()
            val patch = """
                [
                    { "op": "replace", "path": "/id", "value": "1234" }
                ]
            """.trimIndent()

            // Attempt to patch the invitation by ID
            mockMvc.perform(
                patch(
                    "/users/{userId}/invitations/{invitationId}",
                    recipientUserId,
                    invitation.id,
                )
                    .contentType("application/json-patch+json")
                    .content(patch)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun `cannot change invitation creation timestamp`() {
            // Create an invitation

            // Attempt to patch the invitation by ID

            // Anticipate a bad request response
        }

        @Test
        fun `cannot change invitation last-updated timestamp`() {
            // Create an invitation

            // Attempt to patch the invitation by ID

            // Anticipate a bad request response
        }

        @Test
        fun `cannot change invitation event`() {
            // Create an invitation

            // Attempt to patch the invitation by ID

            // Anticipate a bad request response
        }

        @Test
        fun `cannot change invitation recipient`() {
            // Create an invitation

            // Attempt to patch the invitation by ID

            // Anticipate a bad request response
        }

        @Test
        fun `can change invitation status`() {
            // Create a pending invitation

            // Patch an invitation to be accepted

            // Get the invitation and ensure that the status is accepted
        }

        private fun testPatchRequest(jsonPatch: String) {

        }
    }
}