package com.samsul.moviedb.data.remote

import com.samsul.moviedb.data.remote.dto.GenreListResponse
import com.samsul.moviedb.data.remote.dto.MovieDetailDto
import com.samsul.moviedb.data.remote.dto.MovieListResponse
import com.samsul.moviedb.data.remote.dto.ReviewListResponse
import com.samsul.moviedb.data.remote.dto.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreListResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int? = null,
        @Query("page") page: Int,
        @Query("sort_by") sortBy: String? = null
    ): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: Int
    ): MovieDetailDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): ReviewListResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int
    ): VideoListResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): MovieListResponse
}
