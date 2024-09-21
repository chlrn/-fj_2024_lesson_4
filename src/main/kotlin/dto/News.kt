package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.exp

@Serializable
data class Place(
    val id: Int
)

@Serializable
data class News(
    val id: Int,
    val title: String,
    val place: Place? = null,
    val description: String? = null,

    @SerialName("site_url") val siteUrl: String? = null,
    @SerialName("favorites_count") val favoritesCount: Int? = null,
    @SerialName("comments_count") val commentsCount: Int? = null,
    @SerialName("publication_date") val publicationDate: Long? = null // Обратите внимание на nullable
) {
    val rating: Double
        get() {
            val favCount = favoritesCount ?: 0
            val commCount = commentsCount ?: 0
            return 1 / (1 + exp(-(favCount.toDouble() / (commCount + 1).toDouble())))
        }
}
