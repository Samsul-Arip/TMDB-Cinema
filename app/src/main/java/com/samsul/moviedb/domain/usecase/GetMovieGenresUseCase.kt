package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieGenresUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(): Flow<Resource<List<Genre>>> {
        return repository.getMovieGenres()
    }
}
