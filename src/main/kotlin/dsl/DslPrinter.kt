package com.example.dsl

import com.example.dto.News

fun newsDsl(block: DslBuilder.() -> Unit) {
    val builder = DslBuilder()
    builder.block()
    println(builder.toString())
}

class DslBuilder {
    private val sb = StringBuilder()

    fun header(level: Int, text: String) {
        sb.appendLine("${"#".repeat(level)} $text")
    }

    fun newsItem(news: News) {
        sb.appendLine("Title: ${news.title}")
        sb.appendLine("Place: ${news.place}")
        sb.appendLine("Description: ${news.description}")
        sb.appendLine("URL: ${news.siteUrl}")
        sb.appendLine("Favorites: ${news.favoritesCount}")
        sb.appendLine("Comments: ${news.commentsCount}")
        sb.appendLine("Rating: ${news.rating}")
        sb.appendLine()
    }

    override fun toString(): String = sb.toString()
}
