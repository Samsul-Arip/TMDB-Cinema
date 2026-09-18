package com.samsul.moviedb.presentation.navigation

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.samsul.moviedb.presentation.detail.MovieDetailScreen
import com.samsul.moviedb.presentation.genre.GenreScreen
import com.samsul.moviedb.presentation.genre.all.AllGenresScreen
import com.samsul.moviedb.presentation.movielist.MovieListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Genres.route,
        modifier = modifier
    ) {
        composable(
            route = Screen.Genres.route,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ) {
            GenreScreen(
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
                onViewAllGenresClick = {
                    navController.navigate(Screen.AllGenres.route)
                }
            )
        }

        composable(
            route = Screen.AllGenres.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) {
            AllGenresScreen(
                onGenreClick = { genreId, genreName ->
                    navController.navigate(Screen.MovieList.createRoute(genreId, genreName))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MovieList.route,
            arguments = listOf(
                navArgument(Screen.ARG_GENRE_ID) { type = NavType.IntType },
                navArgument(Screen.ARG_GENRE_NAME) { type = NavType.StringType }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getInt(Screen.ARG_GENRE_ID) ?: 0
            val encodedGenreName = backStackEntry.arguments?.getString(Screen.ARG_GENRE_NAME) ?: ""
            val genreName = Uri.decode(encodedGenreName)

            MovieListScreen(
                genreId = genreId,
                genreName = genreName,
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.MovieDetail.route,
            arguments = listOf(
                navArgument(Screen.ARG_MOVIE_ID) { type = NavType.IntType }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(Screen.ARG_MOVIE_ID) ?: 0

            MovieDetailScreen(
                movieId = movieId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
