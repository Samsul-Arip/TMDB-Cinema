package com.samsul.moviedb.presentation.genre

import com.samsul.moviedb.data.repository.FakeMovieRepository
import com.samsul.moviedb.domain.usecase.GetDiscoverMoviesUseCase
import com.samsul.moviedb.domain.usecase.GetMovieGenresUseCase
import com.samsul.moviedb.domain.usecase.SearchMoviesUseCase
import com.samsul.moviedb.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class GenreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private lateinit var getMovieGenresUseCase: GetMovieGenresUseCase
    private lateinit var getDiscoverMoviesUseCase: GetDiscoverMoviesUseCase
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var viewModel: GenreViewModel

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        getMovieGenresUseCase = GetMovieGenresUseCase(repository)
        getDiscoverMoviesUseCase = GetDiscoverMoviesUseCase(repository)
        searchMoviesUseCase = SearchMoviesUseCase(repository)
        viewModel = GenreViewModel(
            getMovieGenresUseCase = getMovieGenresUseCase,
            getDiscoverMoviesUseCase = getDiscoverMoviesUseCase,
            searchMoviesUseCase = searchMoviesUseCase
        )
    }

    @Test
    fun `initialization loads genres and movies successfully`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoadingGenres)
        assertFalse(state.isLoadingMovies)
        assertEquals(2, state.genres.size)
        assertEquals("Action", state.genres.first().name)
        assertEquals(1, state.movies.size)
        assertEquals("Test Movie", state.movies.first().title)
    }

    @Test
    fun `selectGenre updates selectedGenreId and reloads movies`() {
        viewModel.selectGenre(28)
        val state = viewModel.uiState.value
        assertEquals(28, state.selectedGenreId)
        assertFalse(state.isLoadingMovies)
        assertEquals(1, state.movies.size)
    }

    @Test
    fun `refresh reloads both genres and movies`() {
        viewModel.refresh()
        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals(2, state.genres.size)
        assertEquals(1, state.movies.size)
    }

    @Test
    fun `toggleSearch toggles search state and resets query`() {
        assertFalse(viewModel.uiState.value.isSearchActive)

        viewModel.toggleSearch()
        assertTrue(viewModel.uiState.value.isSearchActive)

        viewModel.onSearchQueryChange("Test")
        assertEquals("Test", viewModel.uiState.value.searchQuery)

        viewModel.toggleSearch()
        assertFalse(viewModel.uiState.value.isSearchActive)
        assertEquals("", viewModel.uiState.value.searchQuery)
        assertTrue(viewModel.uiState.value.searchResults.isEmpty())
    }

    @Test
    fun `onSearchQueryChange with valid query triggers search after debounce`() = runTest {
        viewModel.onSearchQueryChange("Test")
        assertEquals("Test", viewModel.uiState.value.searchQuery)

        advanceTimeBy(500.milliseconds)

        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertEquals(1, state.searchResults.size)
        assertEquals("Test Movie", state.searchResults.first().title)
    }

    @Test
    fun `onSearchQueryChange with blank query clears search results`() {
        viewModel.onSearchQueryChange("")
        val state = viewModel.uiState.value
        assertFalse(state.isSearching)
        assertTrue(state.searchResults.isEmpty())
    }

    @Test
    fun `retry reloads data after error`() {
        repository.shouldReturnError = true
        val errorViewModel = GenreViewModel(
            getMovieGenresUseCase = getMovieGenresUseCase,
            getDiscoverMoviesUseCase = getDiscoverMoviesUseCase,
            searchMoviesUseCase = searchMoviesUseCase
        )
        assertNotNull(errorViewModel.uiState.value.errorMessage)

        repository.shouldReturnError = false
        errorViewModel.retry()
        assertNull(errorViewModel.uiState.value.errorMessage)
        assertEquals(2, errorViewModel.uiState.value.genres.size)
        assertEquals(1, errorViewModel.uiState.value.movies.size)
    }
}
