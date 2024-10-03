package com.example.filter

import com.example.dto.News
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.Instant

fun List<News>.getMostRatedNews(count: Int, period: ClosedRange<LocalDate>): List<News> {
    // Преобразование периода в Unix timestamp
    val startTimestamp = period.start.atStartOfDay(ZoneOffset.UTC).toInstant().epochSecond
    val endTimestamp = period.endInclusive.atTime(23, 59, 59).atZone(ZoneOffset.UTC).toInstant().epochSecond

    return this
        .filter { news ->
            // Проверка, что publicationDate не null и попадает в указанный период
            val publicationDate = news.publicationDate ?: return@filter false
            publicationDate in startTimestamp..endTimestamp
        }
        .sortedByDescending { it.rating }
        .take(count)
}
