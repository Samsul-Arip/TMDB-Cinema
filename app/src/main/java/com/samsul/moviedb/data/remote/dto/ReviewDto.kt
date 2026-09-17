package com.samsul.moviedb.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ReviewListResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<ReviewDto>?,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)

data class ReviewDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("author")
    val author: String?,
    @SerializedName("author_details")
    val authorDetails: AuthorDetailsDto?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("created_at")
    val createdAt: String?
)

data class AuthorDetailsDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("avatar_path")
    val avatarPath: String?,
    @SerializedName("rating")
    val rating: Double?
)
