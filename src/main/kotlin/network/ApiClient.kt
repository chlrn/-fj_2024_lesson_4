package com.example.network

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
import java.lang.RuntimeException
import org.slf4j.LoggerFactory

class ApiException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

object ApiClient {
    private const val BASE_URL = "https://kudago.com/public-api/v1.4/news/"

    private val client = HttpClient {
        // Настройка клиента, если требуется
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getNews(count: Int = 100): List<News> {
        val response = fetchWithRetries("$BASE_URL?page_size=$count&order_by=-publication_date&location=spb")
        val jsonString = response.bodyAsText()
        return parseNewsResponse(jsonString)
    }

    suspend fun getNewsDetail(newsId: Int): News {
        val response = fetchWithRetries("$BASE_URL/$newsId/?fields=id,title,place,description,site_url,favorites_count,comments_count,publication_date")
        val jsonString = response.bodyAsText()
        return parseNewsDetail(jsonString)
    }

    private suspend fun fetchWithRetries(url: String, retries: Int = 3, delayMillis: Long = 2000): HttpResponse {
        var lastException: Exception? = null
        repeat(retries) { attempt ->
            try {
                val response: HttpResponse = client.get(url)
                if (response.status == HttpStatusCode.OK) {
                    return response
                } else if (response.status == HttpStatusCode.ServiceUnavailable) {
                    logRetry(attempt)
                    delay(delayMillis)
                } else {
                    handleError(response.status)
                }
            } catch (e: Exception) {
                lastException = e
                logRetry(attempt, e)
                delay(delayMillis)
            }
        }
        throw ApiException("Failed to fetch data after $retries attempts", lastException)
    }

    private fun handleError(status: HttpStatusCode) {
        throw ApiException("Error fetching data: $status")
    }
    private val logger = LoggerFactory.getLogger(ApiClient::class.java) // Инициализация логгера

    private fun logRetry(attempt: Int, e: Exception? = null) {
        val message = "Attempt ${attempt + 1} failed" + (e?.let { ": ${it.message}" } ?: "")
        logger.warn(message) // Используем логирование вместо println
    }

    private fun parseNewsResponse(jsonString: String): List<News> {
        return try {
            val newsResponse = json.decodeFromString<NewsResponse>(jsonString)
            newsResponse.results
        } catch (e: Exception) {
            throw ApiException("Error parsing news: ${e.message}", e)
        }
    }

    private fun parseNewsDetail(jsonString: String): News {
        return try {
            json.decodeFromString<News>(jsonString)
        } catch (e: Exception) {
            throw ApiException("Error parsing news detail: ${e.message}", e)
        }
    }
}
