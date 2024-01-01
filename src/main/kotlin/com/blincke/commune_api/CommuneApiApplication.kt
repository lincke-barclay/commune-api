package com.blincke.commune_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CommuneApiApplication

fun main(args: Array<String>) {
    runApplication<CommuneApiApplication>(*args)
}
