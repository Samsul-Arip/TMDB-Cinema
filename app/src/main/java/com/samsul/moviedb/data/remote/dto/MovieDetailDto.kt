package com.samsul.moviedb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("overview")
    val overview: String?,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?,
    @SerializedName("vote_average")
    val voteAverage: Double?,
    @SerializedName("vote_count")
    val voteCount: Int?,
    @SerializedName("runtime")
    val runtime: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("genres")
    val genres: List<GenreDto>?
)
