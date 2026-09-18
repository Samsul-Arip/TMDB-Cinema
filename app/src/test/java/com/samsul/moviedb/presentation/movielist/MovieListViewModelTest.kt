package com.samsul.moviedb.presentation.movielist

import com.samsul.moviedb.data.repository.FakeMovieRepository
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.usecase.GetDiscoverMoviesUseCase
import com.samsul.moviedb.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private lateinit var getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase
    private lateinit var viewModel: MovieListViewModel

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(repository)
        viewModel = MovieListViewModel(
            genreId = 28,
            getDiscoverMoviesUseCase = getDiscoverMoviesUseCase
        )
    }

    @Test
    fun `initialization loads movies for genre successfully`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.movies.size)
        assertEquals("Test Movie", state.movies.first().title)
        assertEquals(1, state.currentPage)
        assertTrue(state.canPaginate)
        assertNull(state.errorMessage)
    }

    @Test
    fun `loadNextPage increments page and appends new movies`() {
        repository.fakeMovies = listOf(
            Movie(
                id = 2,
                title = "Second Movie",
                overview = "Second Overview",
                posterPath = null,
                backdropPath = null,
                releaseDate = null,
                voteAverage = 7.0,
                voteCount = 100,
                genreIds = listOf(28)
            )
        )

        viewModel.loadNextPage()
        val state = viewModel.uiState.value
        assertEquals(2, state.currentPage)
        assertEquals(2, state.movies.size)
        assertFalse(state.isLoadingMore)
    }

    @Test
    fun `refresh reloads movies with reset`() {
        viewModel.refresh()
        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals(1, state.movies.size)
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `loadMovies failure updates errorMessage`() {
        repository.shouldReturnError = true
        val errorViewModel = MovieListViewModel(
            genreId = 28,
            getDiscoverMoviesUseCase = getDiscoverMoviesUseCase
        )
        val state = errorViewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertEquals(0, state.movies.size)
    }
}
