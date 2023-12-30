package com.blincke.commune_api

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles(value = ["test"])
class CommuneApiApplicationTests {
    @Test
    fun contextLoads() {
    }
}