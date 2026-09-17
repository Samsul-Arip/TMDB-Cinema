package com.samsul.moviedb.data.repository

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer
import com.samsul.moviedb.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeMovieRepository : MovieRepository {

    var shouldReturnError = false
    var fakeGenres = listOf(
        Genre(id = 28, name = "Action"),
        Genre(id = 12, name = "Adventure")
    )
    var fakeMovies = listOf(
        Movie(
            id = 1,
            title = "Test Movie",
            overview = "Test Overview",
            posterPath = "/test_poster.jpg",
            backdropPath = "/test_backdrop.jpg",
            releaseDate = "2024-01-01",
            voteAverage = 8.5,
            voteCount = 500,
            genreIds = listOf(28)
        )
    )
    var fakeDetail = MovieDetail(
        id = 1,
        title = "Test Movie Detail",
        overview = "Detailed overview",
        posterPath = "/poster.jpg",
        backdropPath = "/backdrop.jpg",
        releaseDate = "2024-01-01",
        voteAverage = 8.5,
        voteCount = 500,
        runtime = 120,
        status = "Released",
        genres = listOf(Genre(28, "Action"))
    )
    var fakeReviews = listOf(
        Review(
            id = "r1",
            author = "Critic",
            content = "Great movie!",
            createdAt = "2024-01-02",
            avatarPath = null,
            rating = 9.0
        )
    )
    var fakeTrailers = listOf(
        Trailer(
            id = "t1",
            key = "dQw4w9WgXcQ",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer",
            isOfficial = true
        )
    )

    override fun getMovieGenres(): Flow<Resource<List<Genre>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeGenres))
        }
    }

    override fun getDiscoverMovies(
        genreId: Int?,
        page: Int,
        reset: Boolean
    ): Flow<Resource<List<Movie>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeMovies))
        }
    }

    override fun getLatestMovies(genreId: Int?): Flow<Resource<List<Movie>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeMovies))
        }
    }

    override fun getPopularMovies(genreId: Int?): Flow<Resource<List<Movie>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeMovies))
        }
    }

    override fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeDetail))
        }
    }

    override fun getMovieReviews(
        movieId: Int,
        page: Int,
        reset: Boolean
    ): Flow<Resource<List<Review>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeReviews))
        }
    }

    override fun getMovieTrailers(movieId: Int): Flow<Resource<List<Trailer>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            emit(Resource.Success(fakeTrailers))
        }
    }

    override fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>> = flow {
        if (shouldReturnError) {
            emit(Resource.Error("Network error"))
        } else {
            val filtered = fakeMovies.filter { it.title.contains(query, ignoreCase = true) }
            emit(Resource.Success(filtered))
        }
    }
}
