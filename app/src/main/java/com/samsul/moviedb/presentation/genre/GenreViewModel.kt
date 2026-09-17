package com.samsul.moviedb.presentation.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.usecase.GetDiscoverMoviesUseCase
import com.samsul.moviedb.domain.usecase.GetMovieGenresUseCase
import com.samsul.moviedb.domain.usecase.SearchMoviesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenreViewModel(
    private val getMovieGenresUseCase: GetMovieGenresUseCase,
    private val getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase,
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GenreUiState(
            isLoadingGenres = true,
            isLoadingMovies = true
        )
    )
    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    private var movieJob: Job? = null
    private var searchJob: Job? = null

    init {
        loadGenres()
        loadMovies(genreId = 0, page = 1, reset = false)
    }

    fun loadGenres() {
        viewModelScope.launch {
            getMovieGenresUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoadingGenres = result.isLoading) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoadingGenres = false,
                                genres = result.data ?: emptyList(),
                                isFromCache = false
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoadingGenres = false,
                                genres = result.data ?: it.genres,
                                errorMessage = if (result.data.isNullOrEmpty() && it.movies.isEmpty()) result.message else null,
                                isFromCache = result.isFromCache && (result.data?.isNotEmpty() == true || it.movies.isNotEmpty())
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectGenre(genreId: Int) {
        if (_uiState.value.selectedGenreId == genreId) return
        _uiState.update {
            it.copy(
                selectedGenreId = genreId,
                movies = emptyList(),
                currentPage = 1,
                canPaginate = true,
                isLoadingMovies = true,
                errorMessage = null,
                isFromCache = false
            )
        }
        loadMovies(genreId = genreId, page = 1, reset = false)
    }

    fun refresh() {
        loadGenres()
        loadMovies(genreId = _uiState.value.selectedGenreId, page = 1, reset = true, isRefresh = true)
    }

    fun loadMovies(
        genreId: Int = _uiState.value.selectedGenreId,
        page: Int = 1,
        reset: Boolean = false,
        isRefresh: Boolean = false
    ) {
        if (page == 1) {
            movieJob?.cancel()
        }
        movieJob = viewModelScope.launch {
            if (page == 1) {
                _uiState.update {
                    it.copy(
                        isLoadingMovies = !isRefresh && it.movies.isEmpty(),
                        isRefreshing = isRefresh,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            val apiGenreId = if (genreId == 0) null else genreId
            getDiscoverMoviesUseCase(genreId = apiGenreId, page = page, reset = reset).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        if (page == 1 && !isRefresh) {
                            _uiState.update { it.copy(isLoadingMovies = result.isLoading) }
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
                                isLoadingMovies = false,
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
                                isLoadingMovies = false,
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

    fun loadNextMoviePage() {
        val state = _uiState.value
        if (!state.isLoadingMovies && !state.isLoadingMore && state.canPaginate) {
            loadMovies(genreId = state.selectedGenreId, page = state.currentPage + 1, reset = false)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.update {
                it.copy(
                    isSearching = false,
                    searchResults = emptyList()
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)
            _uiState.update { it.copy(isSearching = true) }
            searchMoviesUseCase(query = query.trim()).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isSearching = result.isLoading) }
                    }
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                searchResults = result.data ?: emptyList()
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                searchResults = result.data ?: emptyList()
                            )
                        }
                    }
                }
            }
        }
    }

    fun toggleSearch() {
        _uiState.update {
            val newActive = !it.isSearchActive
            if (!newActive) {
                searchJob?.cancel()
                it.copy(
                    isSearchActive = false,
                    searchQuery = "",
                    isSearching = false,
                    searchResults = emptyList()
                )
            } else {
                it.copy(isSearchActive = true)
            }
        }
    }

    fun retry() {
        if (_uiState.value.isSearchActive && _uiState.value.searchQuery.isNotBlank()) {
            onSearchQueryChange(_uiState.value.searchQuery)
            return
        }
        if (_uiState.value.genres.isEmpty()) {
            loadGenres()
        }
        loadMovies(genreId = _uiState.value.selectedGenreId, page = 1, reset = true)
    }
}
