package com.samsul.moviedb.presentation.movielist

import com.samsul.moviedb.domain.model.Movie

data class MovieListUiState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val currentPage: Int = 1,
    val canPaginate: Boolean = true,
    val errorMessage: String? = null,
    val isFromCache: Boolean = false,
    val isRefreshing: Boolean = false
)
