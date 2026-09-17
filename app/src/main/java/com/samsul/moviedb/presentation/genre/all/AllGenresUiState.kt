package com.samsul.moviedb.presentation.genre.all

import com.samsul.moviedb.domain.model.Genre

data class AllGenresUiState(
    val isLoading: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val errorMessage: String? = null,
    val isFromCache: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = ""
) {
    val filteredGenres: List<Genre>
        get() {
            val list = if (searchQuery.isBlank()) genres else genres.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
            return list.distinctBy { it.id }
        }
}
