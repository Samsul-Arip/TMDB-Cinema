package com.samsul.moviedb.domain.model

import com.samsul.moviedb.BuildConfig

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genreIds: List<Int> = emptyList()
) {
    val fullPosterUrl: String?
        get() = posterPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w500$it" }

    val fullBackdropUrl: String?
        get() = backdropPath?.let { "${BuildConfig.TMDB_IMAGE_BASE_URL}w780$it" }
}
