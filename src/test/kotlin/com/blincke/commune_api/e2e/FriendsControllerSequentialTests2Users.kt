package com.blincke.commune_api.e2e

import com.blincke.commune_api.e2e.services.TestUsersFixtureService
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = ["test"])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class FriendsControllerSequentialTests2Users {
    @LocalServerPort
    var port: Int? = null

    @Autowired
    lateinit var testUsersFixtureService: TestUsersFixtureService

    lateinit var tokenUser1: String
    lateinit var idUser1: String

    lateinit var tokenUser2: String
    lateinit var idUser2: String

    private val testRestTemplate = TestRestTemplate()

    @BeforeAll
    fun tokens() {
        var ret = testUsersFixtureService.postTokenForUser1()
        tokenUser1 = ret["token"]!!
        idUser1 = ret["id"]!!

        ret = testUsersFixtureService.postTokenForUser2()
        tokenUser2 = ret["token"]!!
        idUser2 = ret["id"]!!
    }

    @Test
    @Order(1)
    fun unAuthenticated401Response() {
        val response = testRestTemplate.getForEntity("http://localhost:${port}", String::class.java)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }

    @Test
    @Order(2)
    fun getUser1() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser1")
        val entity = HttpEntity<Any>(headers)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1",
            HttpMethod.GET,
            entity,
            ObjectNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(idUser1, response.body?.path("id")?.textValue())
        Assertions.assertNotNull(response.body?.path("email")?.textValue())
    }

    @Test
    @Order(3)
    fun getUser2() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2",
            HttpMethod.GET,
            entity,
            ObjectNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(idUser2, response.body?.path("id")?.textValue())
        Assertions.assertNotNull(response.body?.path("email")?.textValue())
    }

    @Test
    @Order(4)
    fun getUser2FromUser1() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1",
            HttpMethod.GET,
            entity,
            ObjectNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(idUser1, response.body?.path("id")?.textValue())
        Assertions.assertNull(response.body?.path("email")?.textValue())
    }

    @Test
    @Order(5)
    fun `get user 1 friends should all be empty`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser1")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())
    }

    @Test
    @Order(6)
    fun `get user 2 friends should all be empty`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())
    }

    @Test
    @Order(7)
    fun `make friend request invalid direction is forbidden`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)
        val response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/$idUser2",
            HttpMethod.POST,
            entity,
            Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    @Order(8)
    fun `make friend request from user 2 to user 1 for the first time should be ok`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)

        // First time created
        Assertions.assertEquals(
            HttpStatus.CREATED,
            testRestTemplate.exchange(
                "http://localhost:${port}/users/$idUser2/friends/$idUser1",
                HttpMethod.POST,
                entity,
                Any::class.java
            ).statusCode
        )

        // Second time no content
        Assertions.assertEquals(
            HttpStatus.NO_CONTENT,
            testRestTemplate.exchange(
                "http://localhost:${port}/users/$idUser2/friends/$idUser1",
                HttpMethod.POST,
                entity,
                Any::class.java
            ).statusCode
        )
    }

    @Test
    @Order(9)
    fun `get user 1 friends after 1st request`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser1")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(1, response.body?.size())
        Assertions.assertEquals(idUser2, response.body?.get(0)?.path("id")?.textValue())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())
    }

    @Test
    @Order(10)
    fun `get user 2 friends after 1st request`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(1, response.body?.size())
        Assertions.assertEquals(idUser1, response.body?.get(0)?.path("id")?.textValue())
    }

    @Test
    @Order(11)
    fun `complete the circuit`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser1")
        val entity = HttpEntity<Any>(headers)

        // First time created
        Assertions.assertEquals(
            HttpStatus.CREATED,
            testRestTemplate.exchange(
                "http://localhost:${port}/users/$idUser1/friends/$idUser2",
                HttpMethod.POST,
                entity,
                Any::class.java
            ).statusCode
        )

        // Second time no content
        Assertions.assertEquals(
            HttpStatus.NO_CONTENT,
            testRestTemplate.exchange(
                "http://localhost:${port}/users/$idUser1/friends/$idUser2",
                HttpMethod.POST,
                entity,
                Any::class.java
            ).statusCode
        )
    }


    @Test
    @Order(12)
    fun `get user 1 friends after completed circuit`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser1")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(1, response.body?.size())
        Assertions.assertEquals(idUser2, response.body?.get(0)?.path("id")?.textValue())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser1/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())
    }

    @Test
    @Order(13)
    fun `get user 2 friends after completed circuit`() {
        val headers = HttpHeaders()
        headers.add("Authorization", "Bearer $tokenUser2")
        val entity = HttpEntity<Any>(headers)

        // Confirmed
        var response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(1, response.body?.size())
        Assertions.assertEquals(idUser1, response.body?.get(0)?.path("id")?.textValue())

        // To Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/to-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())

        // From Me
        response = testRestTemplate.exchange(
            "http://localhost:${port}/users/$idUser2/friends/pending/from-me?page=0&pageSize=20",
            HttpMethod.GET,
            entity,
            ArrayNode::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
        Assertions.assertEquals(0, response.body?.size())
    }
}

