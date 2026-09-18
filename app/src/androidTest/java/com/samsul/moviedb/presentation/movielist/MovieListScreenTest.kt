package com.samsul.moviedb.presentation.movielist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.preview.PreviewConstants
import com.samsul.moviedb.ui.preview.PreviewData
import com.samsul.moviedb.ui.theme.MovieAndroidTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun movieListContent_displaysGenreTitleAndBackButton() {
        var backClicked = false
        val genreName = PreviewConstants.MOCK_GENRE_ACTION

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieListContent(
                    uiState = MovieListUiState(
                        isLoading = false,
                        movies = PreviewData.movies
                    ),
                    genreName = genreName,
                    onMovieClick = {},
                    onBackClick = { backClicked = true },
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        // Title: "%1$s Movies"
        val expectedTitle = context.getString(R.string.movies_in_genre, genreName)
        composeTestRule.onNodeWithText(expectedTitle).assertIsDisplayed()

        // Back button
        val backDesc = context.getString(R.string.content_desc_back)
        composeTestRule.onNodeWithContentDescription(backDesc).performClick()
        assertTrue(backClicked)
    }

    @Test
    fun movieListContent_displaysMovieCards_andHandlesClick() {
        var clickedMovieId = -1

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieListContent(
                    uiState = MovieListUiState(
                        isLoading = false,
                        movies = PreviewData.movies
                    ),
                    genreName = PreviewConstants.MOCK_GENRE_ACTION,
                    onMovieClick = { clickedMovieId = it },
                    onBackClick = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        // Verify movie titles displayed
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_MOVIE_TITLE_1).assertIsDisplayed()

        // Click movie card
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_MOVIE_TITLE_1).performClick()
        assertEquals(PreviewData.movies[0].id, clickedMovieId)
    }

    @Test
    fun movieListContent_emptyState_displaysEmptyMessage() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieListContent(
                    uiState = MovieListUiState(
                        isLoading = false,
                        movies = emptyList()
                    ),
                    genreName = PreviewConstants.MOCK_GENRE_ACTION,
                    onMovieClick = {},
                    onBackClick = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        val emptyMsg = context.getString(R.string.empty_movies)
        composeTestRule.onNodeWithText(emptyMsg).assertIsDisplayed()
    }

    @Test
    fun movieListContent_errorState_displaysErrorMessageAndRetry() {
        var retryClicked = false
        val errorMsg = "Network timeout"

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieListContent(
                    uiState = MovieListUiState(
                        isLoading = false,
                        errorMessage = errorMsg,
                        movies = emptyList()
                    ),
                    genreName = PreviewConstants.MOCK_GENRE_ACTION,
                    onMovieClick = {},
                    onBackClick = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = { retryClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()

        val retryBtnText = context.getString(R.string.retry)
        composeTestRule.onNodeWithText(retryBtnText).performClick()
        assertTrue(retryClicked)
    }
}
