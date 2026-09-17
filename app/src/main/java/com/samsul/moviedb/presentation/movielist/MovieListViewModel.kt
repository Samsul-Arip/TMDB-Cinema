package com.samsul.moviedb.presentation.movielist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.usecase.GetDiscoverMoviesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieListViewModel(
    private val genreId: Int,
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState(isLoading = true))
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        loadMovies(page = 1, reset = false)
    }

    fun refresh() {
        loadMovies(page = 1, reset = true, isRefresh = true)
    }

    fun loadMovies(page: Int = 1, reset: Boolean = false, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (page == 1) {
                _uiState.update {
                    it.copy(
                        isLoading = !isRefresh && it.movies.isEmpty(),
                        isRefreshing = isRefresh,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            getDiscoverMoviesUseCase(genreId = genreId, page = page, reset = reset).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (page == 1 && !isRefresh) {
                            _uiState.update { it.copy(isLoading = result.isLoading) }
                        }
                    }
                    is Resource.Success -> {
                        val freshMovies = (result.data ?: emptyList()).distinctBy { it.id }
                        _uiState.update { current ->
                            val combined = if (page == 1) {
                                freshMovies
                            } else {
                                (current.movies + freshMovies).distinctBy { it.id }
                            }
                            current.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                isRefreshing = false,
                                movies = combined,
                                currentPage = page,
                                canPaginate = freshMovies.isNotEmpty(),
                                errorMessage = null,
                                isFromCache = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update { current ->
                            val fallback = (result.data ?: emptyList()).distinctBy { it.id }
                            val movies = if (fallback.isNotEmpty()) fallback else current.movies
                            current.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                isRefreshing = false,
                                movies = movies,
                                errorMessage = if (movies.isEmpty()) result.message else null,
                                isFromCache = result.isFromCache && movies.isNotEmpty()
                            )
                        }
                    }
                }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.isLoading && !state.isLoadingMore && state.canPaginate) {
            loadMovies(page = state.currentPage + 1, reset = false)
        }
    }
}
