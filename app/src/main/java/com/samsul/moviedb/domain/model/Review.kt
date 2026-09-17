package com.samsul.moviedb.domain.model

import com.samsul.moviedb.BuildConfig

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String?,
    val avatarPath: String?,
    val rating: Double?
) {
    val fullAvatarUrl: String?
        get() {
            if (avatarPath.isNullOrBlank()) return null
            return if (avatarPath.startsWith("/http") || avatarPath.startsWith("http")) {
                avatarPath.removePrefix("/")
            } else {
                "${BuildConfig.TMDB_IMAGE_BASE_URL}w185$avatarPath"
            }
        }
}
