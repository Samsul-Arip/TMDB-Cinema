package com.samsul.moviedb.presentation.genre

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class GenreScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun genreContent_displaysTopAppBarAndCategories() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        isLoadingGenres = false,
                        isLoadingMovies = false,
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        // Top App Bar Brand ("Movie" + "DB" rendered in a single AnnotatedString)
        val brandPartOne = context.getString(R.string.brand_part_one)
        val brandPartTwo = context.getString(R.string.brand_part_two)
        val brandFull = "$brandPartOne$brandPartTwo"
        composeTestRule.onNodeWithText(brandFull).assertIsDisplayed()

        // Search Action Icon
        val searchDesc = context.getString(R.string.content_desc_search)
        composeTestRule.onNodeWithContentDescription(searchDesc).assertIsDisplayed()

        // Section Header
        val genresTitle = context.getString(R.string.genres_title)
        val viewAllText = context.getString(R.string.view_all)
        composeTestRule.onNodeWithText(genresTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(viewAllText).assertIsDisplayed()

        // Categories Chips
        val allChip = context.getString(R.string.all_genres)
        composeTestRule.onNodeWithText(allChip).assertIsDisplayed()
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_GENRE_ACTION).assertIsDisplayed()
    }

    @Test
    fun genreContent_clickCategoryChip_invokesOnGenreSelect() {
        var selectedGenreId = -1

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        isLoadingGenres = false,
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = { selectedGenreId = it },
                    onSearchQueryChange = {},
                    onToggleSearch = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(PreviewConstants.MOCK_GENRE_ACTION).performClick()
        assertEquals(28, selectedGenreId)
    }

    @Test
    fun genreContent_clickSearchToggle_invokesOnToggleSearch() {
        var toggleInvoked = false

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = { toggleInvoked = true },
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        val searchDesc = context.getString(R.string.content_desc_search)
        composeTestRule.onNodeWithContentDescription(searchDesc).performClick()
        assertTrue(toggleInvoked)
    }

    @Test
    fun genreContent_whenSearchActive_showsSearchFieldAndHandlesInput() {
        var inputQuery = ""

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        isSearchActive = true,
                        searchQuery = "",
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = { inputQuery = it },
                    onToggleSearch = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        val searchHint = context.getString(R.string.search_hint)
        composeTestRule.onNodeWithText(searchHint).assertIsDisplayed()
        composeTestRule.onNodeWithText(searchHint).performTextInput("Batman")
        assertEquals("Batman", inputQuery)
    }

    @Test
    fun genreContent_displaysMoviesInGrid_andHandlesCardClick() {
        var clickedMovieId = -1

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = { clickedMovieId = it },
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        // Verify movie title displayed
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_MOVIE_TITLE_1).assertIsDisplayed()

        // Perform click on movie card
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_MOVIE_TITLE_1).performClick()
        assertEquals(PreviewData.movies[0].id, clickedMovieId)
    }

    @Test
    fun genreContent_viewAllCategories_invokesCallback() {
        var viewAllClicked = false

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        genres = PreviewData.genres,
                        movies = PreviewData.movies
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = { viewAllClicked = true },
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = {},
                    onRefresh = {},
                    onLoadMore = {},
                    onRetry = {}
                )
            }
        }

        val viewAllText = context.getString(R.string.view_all)
        composeTestRule.onNodeWithText(viewAllText).performClick()
        assertTrue(viewAllClicked)
    }

    @Test
    fun genreContent_emptyState_displaysEmptyMessage() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        isLoadingMovies = false,
                        genres = PreviewData.genres,
                        movies = emptyList()
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = {},
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
    fun genreContent_errorState_displaysErrorMessageAndRetry() {
        var retryClicked = false
        val errorMsg = "Unable to connect to server"

        composeTestRule.setContent {
            MovieAndroidTheme {
                GenreContent(
                    uiState = GenreUiState(
                        isLoadingMovies = false,
                        errorMessage = errorMsg,
                        movies = emptyList()
                    ),
                    onMovieClick = {},
                    onViewAllGenresClick = {},
                    onGenreSelect = {},
                    onSearchQueryChange = {},
                    onToggleSearch = {},
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
