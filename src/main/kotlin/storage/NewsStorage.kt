package com.example.storage

import com.example.dto.News
import java.io.File

fun saveNews(path: String, news: Collection<News>) {
    println("Saving news to file. Number of news items: ${news.size}")
    if (news.isEmpty()) {
        println("No news to save.")
        return
    }

    val file = File(path)
    if (file.exists()) {
        println("File already exists: $path")
        return
    }

    file.printWriter().use { writer ->
        writer.println("id,title,place,description,siteUrl,favoritesCount,commentsCount,rating")
        news.forEach { n ->
            writer.println(
                "${n.id}," +
                        "\"${n.title}\"," +
                        "\"${n.place ?: ""}\"," +
                        "\"${n.description ?: ""}\"," +
                        "${n.siteUrl ?: ""}," +
                        "${n.favoritesCount}," +
                        "${n.commentsCount}," +
                        "${n.rating}"
            )
        }
    }

    println("News successfully saved to file $path")
}
