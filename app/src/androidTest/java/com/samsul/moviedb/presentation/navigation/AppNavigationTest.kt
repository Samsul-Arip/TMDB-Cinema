package com.samsul.moviedb.presentation.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@RunWith(AndroidJUnit4::class)
class AppNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun routes_verifyConstantsAndTemplates() {
        assertEquals("splash", Screen.Splash.route)
        assertEquals("genres", Screen.Genres.route)
        assertEquals("all_genres", Screen.AllGenres.route)
        assertEquals("movies/{genreId}/{genreName}", Screen.MovieList.route)
        assertEquals("movie_detail/{movieId}", Screen.MovieDetail.route)
    }

    @Test
    fun routes_createMovieListRoute_properlyEncodesSpecialCharacters() {
        val routeSimple = Screen.MovieList.createRoute(28, "Action")
        assertEquals("movies/28/Action", routeSimple)

        val routeWithSpaces = Screen.MovieList.createRoute(878, "Science Fiction")
        assertEquals("movies/878/Science%20Fiction", routeWithSpaces)

        val decoded = URLDecoder.decode(routeWithSpaces, StandardCharsets.UTF_8.name())
        assertEquals("movies/878/Science Fiction", decoded)
    }

    @Test
    fun routes_createMovieDetailRoute_createsCorrectRoute() {
        val route = Screen.MovieDetail.createRoute(999)
        assertEquals("movie_detail/999", route)
    }

    @Test
    fun navigation_graphTransitionsAcrossAllRoutes() {
        var navigatedToGenres = false
        var navigatedToAllGenres = false
        var navigatedToMovieList = false
        var navigatedToMovieDetail = false
        var navigatedBack = false

        composeTestRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route
            ) {
                composable(Screen.Splash.route) { }
                composable(Screen.Genres.route) { }
                composable(Screen.AllGenres.route) { }
                composable(Screen.MovieList.route) { }
                composable(Screen.MovieDetail.route) { }
            }

            LaunchedEffect(Unit) {
                assertNotNull(navController)

                // 1. Splash -> Genres
                navController.navigate(Screen.Genres.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
                navigatedToGenres = navController.currentDestination?.route == Screen.Genres.route

                // 2. Genres -> AllGenres
                navController.navigate(Screen.AllGenres.route)
                navigatedToAllGenres = navController.currentDestination?.route == Screen.AllGenres.route

                // 3. AllGenres -> MovieList
                val movieListRoute = Screen.MovieList.createRoute(28, "Action")
                navController.navigate(movieListRoute)
                navigatedToMovieList = navController.currentDestination?.route == Screen.MovieList.route

                // 4. MovieList -> MovieDetail
                val detailRoute = Screen.MovieDetail.createRoute(123)
                navController.navigate(detailRoute)
                navigatedToMovieDetail = navController.currentDestination?.route == Screen.MovieDetail.route

                // 5. Back Navigation: MovieDetail -> MovieList
                navController.popBackStack()
                navigatedBack = navController.currentDestination?.route == Screen.MovieList.route
            }
        }

        composeTestRule.waitForIdle()

        assertTrue("Should navigate to Genres", navigatedToGenres)
        assertTrue("Should navigate to AllGenres", navigatedToAllGenres)
        assertTrue("Should navigate to MovieList", navigatedToMovieList)
        assertTrue("Should navigate to MovieDetail", navigatedToMovieDetail)
        assertTrue("Should navigate back to MovieList", navigatedBack)
    }
}
