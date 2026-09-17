package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchMoviesUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns matching movies for given query`() = runTest {
        val result = useCase(query = "Test", page = 1).first()
        assertTrue(result is Resource.Success)
        val movies = (result as Resource.Success).data
        assertEquals(1, movies?.size)
        assertEquals("Test Movie", movies?.first()?.title)
    }

    @Test
    fun `invoke returns empty list when query does not match`() = runTest {
        val result = useCase(query = "Nonexistent", page = 1).first()
        assertTrue(result is Resource.Success)
        val movies = (result as Resource.Success).data
        assertEquals(0, movies?.size)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase(query = "Test", page = 1).first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
