package com.samsul.moviedb.core.util

sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null,
    val isFromCache: Boolean = false
) {
    class Success<T>(data: T, isFromCache: Boolean = false) : Resource<T>(data = data, isFromCache = isFromCache)
    class Error<T>(message: String, data: T? = null, isFromCache: Boolean = false) : Resource<T>(data = data, message = message, isFromCache = isFromCache)
    class Loading<T>(val isLoading: Boolean = true, data: T? = null) : Resource<T>(data = data)
}
