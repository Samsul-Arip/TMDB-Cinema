package com.samsul.moviedb.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Splash : Screen(ROUTE_SPLASH)

    data object Genres : Screen(ROUTE_GENRES)

    data object AllGenres : Screen(ROUTE_ALL_GENRES)

    data object MovieList : Screen("movies/{$ARG_GENRE_ID}/{$ARG_GENRE_NAME}") {
        fun createRoute(genreId: Int, genreName: String): String {
            val encodedName = Uri.encode(genreName)
            return "movies/$genreId/$encodedName"
        }
    }

    data object MovieDetail : Screen("movie_detail/{$ARG_MOVIE_ID}") {
        fun createRoute(movieId: Int): String {
            return "movie_detail/$movieId"
        }
    }

    companion object {
        const val ROUTE_SPLASH = "splash"
        const val ROUTE_GENRES = "genres"
        const val ROUTE_ALL_GENRES = "all_genres"
        const val ARG_GENRE_ID = "genreId"
        const val ARG_GENRE_NAME = "genreName"
        const val ARG_MOVIE_ID = "movieId"
    }
}
