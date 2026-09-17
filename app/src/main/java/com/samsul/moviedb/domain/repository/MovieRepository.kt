package com.samsul.moviedb.domain.repository

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovieGenres(): Flow<Resource<List<Genre>>>
    fun getDiscoverMovies(genreId: Int? = null, page: Int, reset: Boolean = false): Flow<Resource<List<Movie>>>
    fun getLatestMovies(genreId: Int? = null): Flow<Resource<List<Movie>>>
    fun getPopularMovies(genreId: Int? = null): Flow<Resource<List<Movie>>>
    fun getMovieDetail(movieId: Int): Flow<Resource<MovieDetail>>
    fun getMovieReviews(movieId: Int, page: Int, reset: Boolean = false): Flow<Resource<List<Review>>>
    fun getMovieTrailers(movieId: Int): Flow<Resource<List<Trailer>>>
    fun searchMovies(query: String, page: Int = 1): Flow<Resource<List<Movie>>>
}
