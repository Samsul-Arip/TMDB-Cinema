package com.samsul.moviedb.domain.model

data class Trailer(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String,
    val isOfficial: Boolean
) {
    val isYouTube: Boolean
        get() = site.equals("YouTube", ignoreCase = true)
}
