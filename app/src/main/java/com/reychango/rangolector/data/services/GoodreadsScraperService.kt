package com.reychango.rangolector.data.services

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoodreadsScraperService @Inject constructor() {
    
    suspend fun searchBooks(query: String): List<GoodreadsBook> {
        return try {
            val searchUrl = "https://www.goodreads.com/search?q=${query.replace(" ", "+")}"
            val doc: Document = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0")
                .get()
            
            doc.select("tr[itemtype='http://schema.org/Book']").map { element ->
                GoodreadsBook(
                    title = element.select("a.bookTitle span").text(),
                    author = element.select("a.authorName span").text(),
                    coverUrl = element.select("img.bookCover").attr("src"),
                    rating = element.select("span.minirating").text()
                        .split("avg").firstOrNull()?.trim()?.toFloatOrNull() ?: 0f,
                    goodreadsUrl = "https://www.goodreads.com" + 
                        element.select("a.bookTitle").attr("href")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class GoodreadsBook(
    val title: String,
    val author: String,
    val coverUrl: String,
    val rating: Float,
    val goodreadsUrl: String
) 