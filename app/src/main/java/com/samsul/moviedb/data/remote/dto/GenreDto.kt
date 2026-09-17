package com.samsul.moviedb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GenreListResponse(
    @SerializedName("genres")
    val genres: List<GenreDto>?
)

data class GenreDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)
