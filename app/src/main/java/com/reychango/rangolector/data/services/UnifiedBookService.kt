package com.reychango.rangolector.data.services

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedBookService @Inject constructor(
    private val openLibraryService: OpenLibraryService,
    private val goodreadsService: GoodreadsScraperService
) {
    suspend fun searchBooks(query: String): List<UnifiedBook> = coroutineScope {
        val openLibraryDeferred = async { 
            try {
                openLibraryService.searchBooks(query).docs.map { book ->
                    UnifiedBook(
                        title = book.title,
                        author = book.mainAuthor,
                        coverUrl = book.coverUrl,
                        rating = null,
                        source = BookSource.OPEN_LIBRARY,
                        sourceUrl = "https://openlibrary.org${book.key}",
                        year = book.first_publish_year
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }
        }

        val goodreadsDeferred = async {
            goodreadsService.searchBooks(query).map { book ->
                UnifiedBook(
                    title = book.title,
                    author = book.author,
                    coverUrl = book.coverUrl,
                    rating = book.rating,
                    source = BookSource.GOODREADS,
                    sourceUrl = book.goodreadsUrl,
                    year = null
                )
            }
        }

        val allBooks = openLibraryDeferred.await() + goodreadsDeferred.await()
        allBooks.distinctBy { it.title.lowercase() to it.author.lowercase() }
    }
}

data class UnifiedBook(
    val title: String,
    val author: String,
    val coverUrl: String?,
    val rating: Float?,
    val source: BookSource,
    val sourceUrl: String,
    val year: Int?
)

enum class BookSource {
    OPEN_LIBRARY,
    GOODREADS
} 