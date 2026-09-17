package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieReviewsUseCase(
    private val repository: MovieRepository
) {
    operator fun invoke(movieId: Int, page: Int, reset: Boolean = false): Flow<Resource<List<Review>>> {
        return repository.getMovieReviews(movieId = movieId, page = page, reset = reset)
    }
}
