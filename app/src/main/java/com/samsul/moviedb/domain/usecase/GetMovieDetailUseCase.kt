package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieDetailUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetail>> {
        return repository.getMovieDetail(movieId)
    }
}
