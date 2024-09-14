package com.example.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val count: Int,
    val next: String? = null,
    val previous: String? = null,
    @SerialName("results") val results: List<News>
)
