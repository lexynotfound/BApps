package com.raihanardila.bapps.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {

    private val iso8601Format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun parseIso8601(dateString: String): Long {
        return iso8601Format.parse(dateString)?.time ?: 0
    }

    fun formatTimeDifference(startTime: Long, endTime: Long): String {
        val duration = endTime - startTime

        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val hours = TimeUnit.MILLISECONDS.toHours(duration)
        val days = TimeUnit.MILLISECONDS.toDays(duration)
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        return when {
            seconds < 1 -> "now"
            seconds < 60 -> "$seconds sec"
            minutes < 60 -> "$minutes min"
            hours < 24 -> "$hours h"
            days < 7 -> "$days d"
            weeks < 4 -> "$weeks week${if (weeks > 1) "s" else ""}"
            months < 12 -> "$months month${if (months > 1) "s" else ""} ago"
            else -> SimpleDateFormat("d MMMM", Locale.getDefault()).format(Date(startTime))
        }
    }
}
