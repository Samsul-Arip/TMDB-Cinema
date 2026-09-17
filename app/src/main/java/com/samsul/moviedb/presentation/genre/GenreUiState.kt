package com.samsul.moviedb.presentation.genre

import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie

data class GenreUiState(
    val isLoadingGenres: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val selectedGenreId: Int = 0, // 0 = All categories
    val isLoadingMovies: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val canPaginate: Boolean = true,
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Movie> = emptyList(),
    val errorMessage: String? = null,
    val isFromCache: Boolean = false,
    val isRefreshing: Boolean = false
) {
    val displayedMovies: List<Movie>
        get() {
            val list = if (searchQuery.isNotBlank()) searchResults else movies
            return list.distinctBy { it.id }
        }

    val filteredMovies: List<Movie>
        get() = displayedMovies
}

