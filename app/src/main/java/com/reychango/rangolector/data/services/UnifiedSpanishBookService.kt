package com.reychango.rangolector.data.services

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnifiedSpanishBookService @Inject constructor(
    private val todosTusLibrosService: TodosTusLibrosService,
    private val casaDelLibroService: CasaDelLibroService,
    private val dilveService: DilveServiceImpl
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

        val dilveDeferred = async {
            try {
                dilveService.searchBooks(query)
            } catch (e: Exception) {
                emptyList()
            }
        }

        val allBooks = todosTusLibrosDeferred.await() + 
                      casaDelLibroDeferred.await() +
                      dilveDeferred.await()
                      
        // Eliminamos duplicados basándonos en el ISBN y damos prioridad a DILVE
        allBooks.groupBy { it.isbn }
            .map { (_, books) ->
                books.firstOrNull { it.source == BookSource.DILVE } ?: books.first()
            }
    }
} 