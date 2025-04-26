package com.reychango.rangolector.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reychango.rangolector.data.services.SpanishBook
import com.reychango.rangolector.data.services.UnifiedSpanishBookService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val bookService: UnifiedSpanishBookService
) : ViewModel() {

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<SpanishBook>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        
        if (query.isBlank()) {
            searchResults = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            try {
                isLoading = true
                error = null
                // Añadimos un pequeño delay para evitar hacer búsquedas mientras el usuario sigue escribiendo
                delay(500)
                searchResults = bookService.searchBooks(query)
            } catch (e: Exception) {
                error = "Error al buscar libros: ${e.message}"
                searchResults = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        error = null
    }

    fun retrySearch() {
        error = null
        onSearchQueryChange(searchQuery)
    }
} 