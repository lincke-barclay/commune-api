package com.blincke.commune_api.com.blincke.commune_api.e2e.tests.api

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = ["test"])
class SmokeTest {
    private val testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var port: Int? = null

    @Test
    fun `unauthenticated 401 response when no token provided`() {
        val response = testRestTemplate.getForEntity("http://localhost:${port}", String::class.java)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}
