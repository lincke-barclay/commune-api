package com.blincke.commune_api.com.blincke.commune_api.util

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ActiveProfiles(value = ["test"])
open class TestBase {
    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    lateinit var dataBootstrapUtility: DataBootstrapUtility
}