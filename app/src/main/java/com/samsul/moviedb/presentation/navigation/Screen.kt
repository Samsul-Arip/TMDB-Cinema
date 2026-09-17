package com.samsul.moviedb.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Genres : Screen("genres")

    data object AllGenres : Screen("all_genres")

    data object MovieList : Screen("movies/{genreId}/{genreName}") {
        fun createRoute(genreId: Int, genreName: String): String {
            val encodedName = Uri.encode(genreName)
            return "movies/$genreId/$encodedName"
        }
    }

    data object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: Int): String {
            return "movie_detail/$movieId"
        }
    }
}
