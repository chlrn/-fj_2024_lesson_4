package com.example

import com.example.api.ApiClient
import com.example.filter.getMostRatedNews
import com.example.storage.saveNews
import com.example.dsl.newsDsl
import com.example.dto.News
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.time.LocalDate

fun main() = runBlocking {
    try {
        // Получение новостей из API
        val newsList: List<News> = ApiClient.getNews(100)

        // Получение деталей для каждой новости
        val detailedNewsList = newsList.map { news ->
            try {
                ApiClient.getNewsDetail(news.id)
            } catch (e: IOException) {
                println("Failed to fetch details for news ID ${news.id}: ${e.message}")
                news.copy(favoritesCount = 0, commentsCount = 0) // Default values if details fetch fails
            }
        }

        // Фильтрация по рейтингу за определённый период
        val mostRatedNews = detailedNewsList.getMostRatedNews(100, LocalDate.now().minusMonths(1)..LocalDate.now())

        // Сохранение новостей в файл
        saveNews("news.csv", mostRatedNews)

        // Вывод новостей в консоль с использованием DSL
        newsDsl {
            header(1, "Top Rated News")
            mostRatedNews.forEach { newsItem(it) }
        }
    } catch (e: Exception) {
        println("An error occurred: ${e.message}")
    }
}
