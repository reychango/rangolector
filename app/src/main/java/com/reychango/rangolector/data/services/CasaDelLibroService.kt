package com.reychango.rangolector.data.services

import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CasaDelLibroService @Inject constructor() {
    
    suspend fun searchBooks(query: String): List<SpanishBook> {
        return try {
            val searchUrl = "https://www.casadellibro.com/busqueda-generica?busqueda=${query.replace(" ", "+")}"
            val doc = Jsoup.connect(searchUrl)
                .userAgent("Mozilla/5.0")
                .get()
            
            doc.select("div.product-item").map { element ->
                val priceText = element.select("div.price").text()
                    .replace("€", "").trim()
                    .replace(",", ".")
                
                SpanishBook(
                    title = element.select("h3.title a").text(),
                    author = element.select("div.author").text(),
                    publisher = element.select("div.publisher").text(),
                    isbn = element.select("div.isbn").text(),
                    coverUrl = element.select("img.product-image").attr("src"),
                    price = priceText.toFloatOrNull(),
                    source = BookSource.CASA_DEL_LIBRO,
                    sourceUrl = "https://www.casadellibro.com" + 
                        element.select("h3.title a").attr("href")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
} 