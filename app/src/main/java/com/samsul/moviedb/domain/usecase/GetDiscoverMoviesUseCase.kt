package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetDiscoverMoviesUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(genreId: Int? = null, page: Int, reset: Boolean = false): Flow<Resource<List<Movie>>> {
        return repository.getDiscoverMovies(genreId = genreId, page = page, reset = reset)
    }
}
