package com.samsul.moviedb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class VideoListResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("results")
    val results: List<VideoDto>?
)

data class VideoDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("key")
    val key: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("site")
    val site: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("official")
    val official: Boolean?
)
