package com.blincke.commune_api.models.network

import org.springframework.data.domain.Sort.Direction

enum class SortDirection(val sortBy: Direction) {
    ASC(Direction.ASC),
    DESC(Direction.DESC),
}