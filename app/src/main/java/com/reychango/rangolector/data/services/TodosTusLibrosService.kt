package com.reychango.rangolector.data.services

import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TodosTusLibrosService @Inject constructor() {
    
    suspend fun searchBooks(query: String): List<SpanishBook> {
        return try {
            val searchUrl = "https://www.todostuslibros.com/busquedas?keyword=${query.replace(" ", "+")}"
            val doc = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0")
                .get()
            
            doc.select("div.libro").map { element ->
                SpanishBook(
                    title = element.select("h2.titulo a").text(),
                    author = element.select("p.autor a").text(),
                    publisher = element.select("p.editorial a").text(),
                    isbn = element.select("p.isbn").text().replace("ISBN: ", ""),
                    coverUrl = element.select("img.portada").attr("src"),
                    price = element.select("p.precio").text()
                        .replace("€", "").trim().toFloatOrNull(),
                    source = BookSource.TODOS_TUS_LIBROS,
                    sourceUrl = "https://www.todostuslibros.com" + 
                        element.select("h2.titulo a").attr("href")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class SpanishBook(
    val title: String,
    val author: String,
    val publisher: String,
    val isbn: String,
    val coverUrl: String?,
    val price: Float?,
    val source: BookSource,
    val sourceUrl: String
) 