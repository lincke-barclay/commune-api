package com.blincke.commune_api.common

import java.time.Instant
import java.time.temporal.ChronoUnit

// TODO - figure this out
fun largestPSQLDate() = Instant.now().plus(365 * 10 * 24 * 60 * 60, ChronoUnit.SECONDS)

