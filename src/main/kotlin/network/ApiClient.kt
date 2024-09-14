package com.example.api

import com.example.dto.News
import com.example.dto.NewsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.*
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.delay
import java.io.IOException

object ApiClient {
    private val client = HttpClient {
        // Настройка клиента, если требуется
    }

    private val json = Json { ignoreUnknownKeys = true }

    private suspend fun fetchWithRetries(url: String, retries: Int = 3, delayMillis: Long = 2000): HttpResponse {
        var lastException: Exception? = null
        repeat(retries) { attempt ->
            try {
                val response: HttpResponse = client.get(url)
                if (response.status == HttpStatusCode.OK) {
                    val responseBody = response.bodyAsText()
                    if (responseBody.isBlank()) {
                        throw IOException("Received an empty response from the API")
                    }
                    return response
                } else if (response.status == HttpStatusCode.ServiceUnavailable) {
                    println("Attempt ${attempt + 1} failed with status 503. Retrying...")
                    delay(delayMillis)
                } else {
                    throw IOException("Error fetching data: ${response.status}")
                }
            } catch (e: Exception) {
                lastException = e
                println("Attempt ${attempt + 1} failed with exception: ${e.message}. Retrying...")
                delay(delayMillis)
            }
        }
        throw IOException("Failed to fetch data after $retries attempts", lastException)
    }

    suspend fun getNews(count: Int = 100): List<News> {
        val response: HttpResponse = fetchWithRetries("https://kudago.com/public-api/v1.4/news/?page_size=$count&order_by=-publication_date&location=spb")
        val jsonString = response.bodyAsText()
        println("Received JSON: $jsonString")
        return try {
            val newsResponse = json.decodeFromString<NewsResponse>(jsonString)
            newsResponse.results
        } catch (e: Exception) {
            throw IOException("Error parsing news: ${e.message}", e)
        }
    }

    suspend fun getNewsDetail(newsId: Int): News {
        val response: HttpResponse = fetchWithRetries("https://kudago.com/public-api/v1.4/news/$newsId/?fields=id,title,place,description,site_url,favorites_count,comments_count,publication_date")
        val jsonString = response.bodyAsText()
        println("Received Detail JSON: $jsonString")
        return try {
            json.decodeFromString<News>(jsonString)
        } catch (e: Exception) {
            throw IOException("Error parsing news detail: ${e.message}", e)
        }
    }
}
