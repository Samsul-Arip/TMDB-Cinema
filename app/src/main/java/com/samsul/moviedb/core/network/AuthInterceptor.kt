package com.samsul.moviedb.core.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        val urlWithApiKey = originalUrl.newBuilder()
            .addQueryParameter("api_key", apiKey)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(urlWithApiKey)
            .addHeader("Accept", "application/json")
            .build()

        return chain.proceed(newRequest)
    }
}
