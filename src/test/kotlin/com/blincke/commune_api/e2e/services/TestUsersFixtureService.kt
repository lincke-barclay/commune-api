package com.blincke.commune_api.e2e.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


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

    fun postTokenForUser1(): Map<String, String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val body = JSONObject()

        body.put("email", test1UserEmail)
        body.put("password", test1UserPassword)
        body.put("returnSecureToken", true)

        val entity: HttpEntity<Any> = HttpEntity(body.toString(), headers)
        val response = restTemplate.postForObject(oauthTokenEndpoint, entity, String::class.java)

        val obj = objectMapper.readTree(response)

        val ret = HashMap<String, String>()
        ret.put("token", obj.path("idToken").asText())
        ret.put("id", obj.path("localId").asText())
        return ret
    }

    fun postTokenForUser2(): Map<String, String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val body = JSONObject()

        body.put("email", test2UserEmail)
        body.put("password", test2UserPassword)
        body.put("returnSecureToken", true)

        val entity: HttpEntity<Any> = HttpEntity(body.toString(), headers)
        val response = restTemplate.postForObject(oauthTokenEndpoint, entity, String::class.java)

        val obj = objectMapper.readTree(response)

        val ret = HashMap<String, String>()
        ret["token"] = obj.path("idToken").asText()
        ret["id"] = obj.path("localId").asText()
        return ret
    }
}