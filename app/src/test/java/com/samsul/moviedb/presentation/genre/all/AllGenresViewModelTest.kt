package com.samsul.moviedb.presentation.genre.all

import com.samsul.moviedb.data.repository.FakeMovieRepository
import com.samsul.moviedb.domain.usecase.GetMovieGenresUseCase
import com.samsul.moviedb.util.MainDispatcherRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AllGenresViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeMovieRepository
    private lateinit var getMovieGenresUseCase: GetMovieGenresUseCase
    private lateinit var viewModel: AllGenresViewModel

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        getMovieGenresUseCase = GetMovieGenresUseCase(repository)
        viewModel = AllGenresViewModel(getMovieGenresUseCase)
    }

    @Test
    fun `initialization loads all genres successfully`() {
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.genres.size)
        assertEquals("Action", state.genres.first().name)
        assertNull(state.errorMessage)
    }

    @Test
    fun `refresh reloads genres`() {
        viewModel.refresh()
        val state = viewModel.uiState.value
        assertFalse(state.isRefreshing)
        assertEquals(2, state.genres.size)
    }

    @Test
    fun `onSearchQueryChange updates query and filteredGenres`() {
        viewModel.onSearchQueryChange("Act")
        val state = viewModel.uiState.value
        assertEquals("Act", state.searchQuery)
        assertEquals(1, state.filteredGenres.size)
        assertEquals("Action", state.filteredGenres.first().name)

        viewModel.onSearchQueryChange("NonExistent")
        assertEquals(0, viewModel.uiState.value.filteredGenres.size)
    }

    @Test
    fun `loadGenres failure updates errorMessage`() {
        repository.shouldReturnError = true
        val errorViewModel = AllGenresViewModel(getMovieGenresUseCase)
        val state = errorViewModel.uiState.value

        assertFalse(state.isLoading)
        assertNotNull(state.errorMessage)
        assertEquals(0, state.genres.size)
    }
}
