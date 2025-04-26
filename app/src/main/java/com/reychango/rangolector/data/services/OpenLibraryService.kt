package com.reychango.rangolector.data.services

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryService {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = "key,title,author_name,cover_i,first_publish_year",
        @Query("limit") limit: Int = 20
    ): OpenLibraryResponse
}

data class OpenLibraryResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<OpenLibraryBook>
)

data class OpenLibraryBook(
    val key: String,
    val title: String,
    val author_name: List<String>? = null,
    val cover_i: Long? = null,
    val first_publish_year: Int? = null
) {
    val mainAuthor: String
        get() = author_name?.firstOrNull() ?: "Autor desconocido"
    
    val coverUrl: String?
        get() = cover_i?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
} 