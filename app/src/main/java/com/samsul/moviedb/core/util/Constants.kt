package com.samsul.moviedb.core.util

/**
 * Application-wide constants for error messages, category types, query parameters, and media types.
 */
object Constants {
    // Network & Error Messages
    const val ERROR_NETWORK_CONNECTION = "Network connection error. Please check your internet connection."
    const val ERROR_FAILED_LOAD_GENRES = "Failed to load genres."
    const val ERROR_FAILED_LOAD_MOVIES = "Failed to load movies."
    const val ERROR_FAILED_LOAD_LATEST = "Failed to load latest movies."
    const val ERROR_FAILED_LOAD_POPULAR = "Failed to load popular movies."
    const val ERROR_FAILED_LOAD_DETAIL = "Failed to load movie details."
    const val ERROR_FAILED_LOAD_REVIEWS = "Failed to load reviews."
    const val ERROR_FAILED_LOAD_TRAILERS = "Failed to load movie trailers."
    const val ERROR_FAILED_SEARCH_MOVIES = "Failed to search movies."

    // Movie Category Types
    const val CATEGORY_DISCOVER = "discover"
    const val CATEGORY_LATEST = "latest"
    const val CATEGORY_POPULAR = "popular"

    // TMDB Query Parameters & Sort Keys
    const val SORT_BY_RELEASE_DATE_DESC = "primary_release_date.desc"
    const val SORT_BY_POPULARITY_DESC = "popularity.desc"

    // Media Types & Providers
    const val VIDEO_TYPE_TRAILER = "Trailer"
    const val VIDEO_SITE_YOUTUBE = "YouTube"
}
