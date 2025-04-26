package com.reychango.rangolector.data.services

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedSpanishBookService @Inject constructor(
    private val todosTusLibrosService: TodosTusLibrosService,
    private val casaDelLibroService: CasaDelLibroService
) {
    suspend fun searchBooks(query: String): List<SpanishBook> = coroutineScope {
        val todosTusLibrosDeferred = async { 
            try {
                todosTusLibrosService.searchBooks(query)
            } catch (e: Exception) {
                emptyList()
            }
        }

        val casaDelLibroDeferred = async {
            try {
                casaDelLibroService.searchBooks(query)
            } catch (e: Exception) {
                emptyList()
            }
        }

        val allBooks = todosTusLibrosDeferred.await() + casaDelLibroDeferred.await()
        // Eliminamos duplicados basándonos en el ISBN
        allBooks.distinctBy { it.isbn }
    }
} 