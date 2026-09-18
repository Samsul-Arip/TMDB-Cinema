package com.samsul.moviedb.presentation.splash

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsul.moviedb.R
import com.samsul.moviedb.ui.theme.MovieAndroidTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieSplashScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun splashScreen_displaysBrandTitleAndLogo() {
        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieSplashScreen(onSplashFinished = {})
            }
        }

        // Verify App Logo
        val logoDesc = context.getString(R.string.content_desc_app_logo)
        composeTestRule.onNodeWithContentDescription(logoDesc).assertIsDisplayed()

        // Verify Brand Title: "Movie" and "DB"
        val brandPartOne = context.getString(R.string.brand_part_one)
        val brandPartTwo = context.getString(R.string.brand_part_two)
        composeTestRule.onNodeWithText(brandPartOne).assertIsDisplayed()
        composeTestRule.onNodeWithText(brandPartTwo).assertIsDisplayed()

        // Verify Tagline
        val tagline = context.getString(R.string.splash_tagline)
        composeTestRule.onNodeWithText(tagline).assertIsDisplayed()

        // Verify TMDB Attribution
        val attribution = context.getString(R.string.splash_tmdb_attribution)
        composeTestRule.onNodeWithText(attribution).assertIsDisplayed()
    }

    @Test
    fun splashScreen_triggersFinishCallbackAfterDelay() {
        var isFinished = false

        composeTestRule.setContent {
            MovieAndroidTheme {
                MovieSplashScreen(onSplashFinished = { isFinished = true })
            }
        }

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.mainClock.advanceTimeBy(1600)
        composeTestRule.waitForIdle()

        assertTrue(isFinished)
    }
}
