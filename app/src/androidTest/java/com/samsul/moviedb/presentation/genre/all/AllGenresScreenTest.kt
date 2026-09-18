package com.samsul.moviedb.presentation.genre.all

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
class AllGenresScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun allGenresContent_displaysTitleAndBackButton() {
        var backClicked = false

        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        genres = PreviewData.genres
                    ),
                    onGenreClick = { _, _ -> },
                    onBackClick = { backClicked = true },
                    onSearchQueryChange = {},
                    onRefresh = {},
                    onRetry = {}
                )
            }
        }

        // Title and Subtitle
        val title = context.getString(R.string.all_genres_title)
        val subtitle = context.getString(R.string.all_genres_subtitle)
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
        composeTestRule.onNodeWithText(subtitle).assertIsDisplayed()

        // Back button
        val backDesc = context.getString(R.string.content_desc_back)
        composeTestRule.onNodeWithContentDescription(backDesc).performClick()
        assertTrue(backClicked)
    }

    @Test
    fun allGenresContent_displaysAllMoviesCardAndGenreCards() {
        var clickedId = -1
        var clickedName = ""

        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        genres = PreviewData.genres
                    ),
                    onGenreClick = { id, name ->
                        clickedId = id
                        clickedName = name
                    },
                    onBackClick = {},
                    onSearchQueryChange = {},
                    onRefresh = {},
                    onRetry = {}
                )
            }
        }

        // "All Movies" card
        val allMoviesLabel = context.getString(R.string.all_movies_label)
        composeTestRule.onNodeWithText(allMoviesLabel).assertIsDisplayed()
        composeTestRule.onNodeWithText(allMoviesLabel).performClick()

        val allGenreName = context.getString(R.string.all_genres)
        assertEquals(0, clickedId)
        assertEquals(allGenreName, clickedName)

        // Genre card
        composeTestRule.onNodeWithText(PreviewConstants.MOCK_GENRE_ACTION).assertIsDisplayed()
    }

    @Test
    fun allGenresContent_clickGenreCard_invokesOnGenreClick() {
        var clickedId = -1
        var clickedName = ""

        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        genres = PreviewData.genres
                    ),
                    onGenreClick = { id, name ->
                        clickedId = id
                        clickedName = name
                    },
                    onBackClick = {},
                    onSearchQueryChange = {},
                    onRefresh = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(PreviewConstants.MOCK_GENRE_ACTION).performClick()
        assertEquals(28, clickedId)
        assertEquals(PreviewConstants.MOCK_GENRE_ACTION, clickedName)
    }

    @Test
    fun allGenresContent_searchGenre_invokesOnSearchQueryChange() {
        var queryInput = ""

        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        searchQuery = "",
                        genres = PreviewData.genres
                    ),
                    onGenreClick = { _, _ -> },
                    onBackClick = {},
                    onSearchQueryChange = { queryInput = it },
                    onRefresh = {},
                    onRetry = {}
                )
            }
        }

        val hint = context.getString(R.string.search_genres_hint)
        composeTestRule.onNodeWithText(hint).assertIsDisplayed()
        composeTestRule.onNodeWithText(hint).performTextInput("Horror")
        assertEquals("Horror", queryInput)
    }

    @Test
    fun allGenresContent_emptyState_displaysEmptyMessage() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        genres = emptyList()
                    ),
                    onGenreClick = { _, _ -> },
                    onBackClick = {},
                    onSearchQueryChange = {},
                    onRefresh = {},
                    onRetry = {}
                )
            }
        }

        val emptyMsg = context.getString(R.string.empty_genres)
        composeTestRule.onNodeWithText(emptyMsg).assertIsDisplayed()
    }

    @Test
    fun allGenresContent_errorState_displaysErrorMessageAndRetry() {
        var retryTriggered = false
        val errorMsg = "Unable to fetch genres"

        composeTestRule.setContent {
            MovieAndroidTheme {
                AllGenresContent(
                    uiState = AllGenresUiState(
                        isLoading = false,
                        errorMessage = errorMsg,
                        genres = emptyList()
                    ),
                    onGenreClick = { _, _ -> },
                    onBackClick = {},
                    onSearchQueryChange = {},
                    onRefresh = {},
                    onRetry = { retryTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()
        val retryBtnText = context.getString(R.string.retry)
        composeTestRule.onNodeWithText(retryBtnText).performClick()
        assertTrue(retryTriggered)
    }
}
