package com.blincke.commune_api.com.blincke.commune_api.e2e.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

data class TestUserFixtureInfo(
    val id: String,
    val token: String,
)

/**
 * These are real users stored in firebase
 * This service fetches their tokens and id's etc.
 */
@Component
class TestUsersFixtureService {
    @Value("\${fixtures.users.test1.email}")
    lateinit var test1UserEmail: String

    @Value("\${fixtures.users.test1.password}")
    lateinit var test1UserPassword: String

    @Value("\${fixtures.users.test2.email}")
    lateinit var test2UserEmail: String

    @Value("\${fixtures.users.test2.password}")
    lateinit var test2UserPassword: String

    val oauthTokenEndpoint =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key= AIzaSyCliGj-HGF60J25hcczptbh_fFPYvuGatg"
    val restTemplate: RestTemplate = RestTemplate()
    val objectMapper = ObjectMapper()

    fun postTokenForUser1(): TestUserFixtureInfo {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val body = JSONObject()

        body.put("email", test1UserEmail)
        body.put("password", test1UserPassword)
        body.put("returnSecureToken", true)

        val entity: HttpEntity<Any> = HttpEntity(body.toString(), headers)
        val response = restTemplate.postForObject(oauthTokenEndpoint, entity, String::class.java)

        val obj = objectMapper.readTree(response)

        return TestUserFixtureInfo(
            id = obj.path("localId").asText(),
            token = obj.path("idToken").asText(),
        )
    }

    fun postTokenForUser2(): TestUserFixtureInfo {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val body = JSONObject()

        body.put("email", test2UserEmail)
        body.put("password", test2UserPassword)
        body.put("returnSecureToken", true)

        val entity: HttpEntity<Any> = HttpEntity(body.toString(), headers)
        val response = restTemplate.postForObject(oauthTokenEndpoint, entity, String::class.java)

        val obj = objectMapper.readTree(response)

        return TestUserFixtureInfo(
            id = obj.path("localId").asText(),
            token = obj.path("idToken").asText(),
        )
    }
}