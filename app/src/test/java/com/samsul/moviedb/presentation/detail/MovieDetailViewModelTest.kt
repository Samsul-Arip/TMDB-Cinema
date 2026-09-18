package com.samsul.moviedb.presentation.detail

import com.samsul.moviedb.data.repository.FakeMovieRepository
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.usecase.GetMovieDetailUseCase
import com.samsul.moviedb.domain.usecase.GetMovieReviewsUseCase
import com.samsul.moviedb.domain.usecase.GetMovieTrailerUseCase
import com.samsul.moviedb.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase
    private lateinit var getMovieReviewsUseCase: GetMovieReviewsUseCase
    private lateinit var getMovieTrailerUseCase: GetMovieTrailerUseCase
    private lateinit var viewModel: MovieDetailViewModel

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        getMovieDetailUseCase = GetMovieDetailUseCase(repository)
        getMovieReviewsUseCase = GetMovieReviewsUseCase(repository)
        getMovieTrailerUseCase = GetMovieTrailerUseCase(repository)
        viewModel = MovieDetailViewModel(
            movieId = 1,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieReviewsUseCase = getMovieReviewsUseCase,
            getMovieTrailerUseCase = getMovieTrailerUseCase
        )
    }

    @Test
    fun `initialization loads detail, trailers, and reviews successfully`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoadingDetail)
        assertNotNull(state.movieDetail)
        assertEquals("Test Movie Detail", state.movieDetail?.title)
        assertNull(state.detailError)

        assertFalse(state.isLoadingTrailers)
        assertEquals(1, state.trailers.size)
        assertEquals("Official Trailer", state.selectedTrailer?.name)

        assertFalse(state.isLoadingReviews)
        assertEquals(1, state.reviews.size)
        assertEquals("Critic", state.reviews.first().author)
        assertEquals(1, state.currentReviewPage)
        assertTrue(state.canPaginateReviews)
    }

    @Test
    fun `loadNextReviewPage increments review page and appends new reviews`() {
        repository.fakeReviews = listOf(
            Review(
                id = "r2",
                author = "Second Reviewer",
                content = "Second opinion",
                createdAt = "2024-01-03",
                avatarPath = null,
                rating = 8.0
            )
        )

        viewModel.loadNextReviewPage()
        val state = viewModel.uiState.value
        assertEquals(2, state.currentReviewPage)
        assertEquals(2, state.reviews.size)
        assertFalse(state.isLoadingMoreReviews)
    }

    @Test
    fun `refresh reloads all movie detail data`() {
        viewModel.refresh()
        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertNotNull(state.movieDetail)
        assertEquals(1, state.trailers.size)
        assertEquals(1, state.reviews.size)
    }

    @Test
    fun `loadDetail failure updates detailError`() {
        repository.shouldReturnError = true
        val errorViewModel = MovieDetailViewModel(
            movieId = 1,
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieReviewsUseCase = getMovieReviewsUseCase,
            getMovieTrailerUseCase = getMovieTrailerUseCase
        )
        val state = errorViewModel.uiState.value

        assertFalse(state.isLoadingDetail)
        assertNull(state.movieDetail)
        assertNotNull(state.detailError)
    }
}
