package com.reychango.rangolector.data.services

import retrofit2.http.*

interface DilveService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): DilveAuthResponse

    @GET("search")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("format") format: String = "json",
        @Header("Authorization") authToken: String
    ): DilveSearchResponse
}

data class DilveAuthResponse(
    val token: String,
    val expiresIn: Long
)

data class DilveSearchResponse(
    val total: Int,
    val items: List<DilveBook>
)

data class DilveBook(
    val isbn: String,
    val title: String,
    val contributors: List<DilveContributor>,
    val publisher: String,
    val publishDate: String,
    val description: String?,
    val subjects: List<String>,
    val coverUrl: String?,
    val price: Float?
) {
    fun toSpanishBook() = SpanishBook(
        title = title,
        author = contributors.firstOrNull { it.role == "author" }?.name ?: "Autor desconocido",
        publisher = publisher,
        isbn = isbn,
        coverUrl = coverUrl,
        price = price,
        source = BookSource.DILVE,
        sourceUrl = "https://www.dilve.es/dilve/dilveweb/participantesfichalib.jsp?id=$isbn"
    )
}

data class DilveContributor(
    val name: String,
    val role: String
) 