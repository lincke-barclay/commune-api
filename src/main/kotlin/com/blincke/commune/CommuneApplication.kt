package com.blincke.commune

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommuneApplication

fun main(args: Array<String>) {
	runApplication<CommuneApplication>(*args)
}
