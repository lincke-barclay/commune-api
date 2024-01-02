package com.blincke.commune_api.com.blincke.commune_api.e2e

import com.blincke.commune_api.e2e.services.TestUsersFixtureService
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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = ["test"])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerSequentialTests2Users {
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
}

