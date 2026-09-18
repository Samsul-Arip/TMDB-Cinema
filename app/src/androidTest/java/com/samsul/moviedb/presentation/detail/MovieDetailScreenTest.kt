package com.samsul.moviedb.presentation.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.preview.PreviewConstants
import com.samsul.moviedb.ui.preview.PreviewData
import com.samsul.moviedb.ui.theme.MovieAndroidTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun movieDetailContent_displaysHeroAndMetadata() {
        var backClicked = false

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = PreviewData.trailers,
                        reviews = PreviewData.reviews
                    ),
                    onBackClick = { backClicked = true },
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        // Movie Title
        composeTestRule.onNodeWithText(PreviewData.movieDetail.title).assertIsDisplayed()

        // Back Button
        val backDesc = context.getString(R.string.content_desc_back)
        composeTestRule.onNodeWithContentDescription(backDesc).performClick()
        assertTrue(backClicked)
    }

    @Test
    fun movieDetailContent_displaysOverview_andExpandsOnReadMore() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = PreviewData.trailers,
                        reviews = PreviewData.reviews
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        val synopsisTitle = context.getString(R.string.synopsis_title)
        composeTestRule.onNodeWithText(synopsisTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(PreviewData.movieDetail.overview).assertIsDisplayed()

        val readMore = context.getString(R.string.read_more)
        val readLess = context.getString(R.string.read_less)
        if (PreviewData.movieDetail.overview.length > 120) {
            composeTestRule.onNodeWithText(readMore).assertIsDisplayed()
            composeTestRule.onNodeWithText(readMore).performClick()
            composeTestRule.onNodeWithText(readLess).assertIsDisplayed()
        }
    }

    @Test
    fun movieDetailContent_displaysTrailerSection_whenTrailerPresent() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = PreviewData.trailers,
                        selectedTrailer = PreviewData.trailers.firstOrNull(),
                        reviews = PreviewData.reviews
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        val trailerTitle = context.getString(R.string.trailer_section_title)
        composeTestRule.onNodeWithText(trailerTitle).performScrollTo().assertIsDisplayed()

        val openYoutube = context.getString(R.string.open_in_youtube)
        composeTestRule.onNodeWithText(openYoutube).performScrollTo().assertIsDisplayed()

        val watchTrailer = context.getString(R.string.watch_trailer_action)
        composeTestRule.onNodeWithText(watchTrailer).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun movieDetailContent_displaysFallback_whenNoTrailer() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = emptyList(),
                        selectedTrailer = null,
                        reviews = PreviewData.reviews
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        val noTrailerMsg = context.getString(R.string.no_trailer_available)
        composeTestRule.onNodeWithText(noTrailerMsg).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun movieDetailContent_displaysUserReviews() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = PreviewData.trailers,
                        selectedTrailer = PreviewData.trailers.firstOrNull(),
                        reviews = PreviewData.reviews
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        composeTestRule.onNodeWithText(PreviewConstants.MOCK_REVIEW_AUTHOR_1).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun movieDetailContent_emptyReviews_displaysFallbackMessage() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = PreviewData.movieDetail,
                        trailers = PreviewData.trailers,
                        selectedTrailer = PreviewData.trailers.firstOrNull(),
                        reviews = emptyList()
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = {},
                    onLoadMoreReviews = {}
                )
            }
        }

        val noReviewsMsg = context.getString(R.string.no_reviews_available)
        composeTestRule.onNodeWithText(noReviewsMsg).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun movieDetailContent_errorState_displaysErrorMessageAndRetry() {
        var retryClicked = false
        val errorMsg = "Failed to load movie details"

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieDetailContent(
                    uiState = MovieDetailUiState(
                        isLoadingDetail = false,
                        movieDetail = null,
                        detailError = errorMsg
                    ),
                    onBackClick = {},
                    onRefresh = {},
                    onRetry = { retryClicked = true },
                    onLoadMoreReviews = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorMsg).assertIsDisplayed()

        val retryBtnText = context.getString(R.string.retry)
        composeTestRule.onNodeWithText(retryBtnText).performClick()
        assertTrue(retryClicked)
    }
}
