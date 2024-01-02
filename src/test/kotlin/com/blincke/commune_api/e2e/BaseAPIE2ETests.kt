package com.blincke.commune_api.com.blincke.commune_api.e2e

import org.junit.jupiter.api.*
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = ["test"])
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BaseAPIE2ETests {
    private val testRestTemplate = TestRestTemplate()

    @LocalServerPort
    var port: Int? = null

    @Test
    fun `unauthenticated 401 response when no token provided`() {
        val response = testRestTemplate.getForEntity("http://localhost:${port}", String::class.java)
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
    }
}