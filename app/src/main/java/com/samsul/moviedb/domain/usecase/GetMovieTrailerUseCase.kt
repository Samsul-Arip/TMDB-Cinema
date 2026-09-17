package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Trailer
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieTrailerUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int): Flow<Resource<List<Trailer>>> {
        return repository.getMovieTrailers(movieId)
    }
}
