package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class SearchMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String, page: Int = 1): Flow<Resource<List<Movie>>> {
        return repository.searchMovies(query = query, page = page)
    }
}
