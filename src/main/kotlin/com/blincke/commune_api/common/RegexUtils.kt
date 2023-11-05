package com.blincke.commune_api.common

operator fun Regex.contains(text: CharSequence): Boolean = this.matches(text)
