package com.samsul.moviedb.presentation.genre.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.usecase.GetMovieGenresUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllGenresViewModel(
    private val getMovieGenresUseCase: GetMovieGenresUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AllGenresUiState(isLoading = true))
    val uiState: StateFlow<AllGenresUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    fun refresh() {
        loadGenres(isRefresh = true)
    }

    fun loadGenres(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            }
            getMovieGenresUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (!isRefresh) {
                            _uiState.update { it.copy(isLoading = result.isLoading) }
                        }
                    }
                    is Resource.Success -> {
                        val distinct = (result.data ?: emptyList()).distinctBy { it.id }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                genres = distinct,
                                errorMessage = null,
                                isFromCache = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        val distinct = (result.data ?: emptyList()).distinctBy { it.id }
                        _uiState.update {
                            val genres = if (distinct.isNotEmpty()) distinct else it.genres
                            it.copy(
                                isLoading = false,
                                isRefreshing = false,
                                genres = genres,
                                errorMessage = if (genres.isEmpty()) result.message else null,
                                isFromCache = result.isFromCache && genres.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
