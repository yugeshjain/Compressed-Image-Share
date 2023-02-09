package com.yugesh.compressedimageshare.util

fun Long.toKbOrMb(): String {
    val convertToKb = this.div(1000)
    return if (convertToKb > 1024) {
        "${convertToKb.div(1024)}mb"
    } else {
        "${convertToKb}kb"
    }
}
