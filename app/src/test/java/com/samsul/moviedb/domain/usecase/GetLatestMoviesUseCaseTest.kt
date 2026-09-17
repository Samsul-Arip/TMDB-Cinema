package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetLatestMoviesUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetLatestMoviesUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetLatestMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns latest movies for given genre`() = runTest {
        val result = useCase(genreId = 28).first()
        assertTrue(result is Resource.Success)
        val movies = (result as Resource.Success).data
        assertEquals(1, movies?.size)
        assertEquals("Test Movie", movies?.first()?.title)
    }

    @Test
    fun `invoke returns latest movies when genreId is null`() = runTest {
        val result = useCase(genreId = null).first()
        assertTrue(result is Resource.Success)
        val movies = (result as Resource.Success).data
        assertEquals(1, movies?.size)
        assertEquals("Test Movie", movies?.first()?.title)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase(genreId = 28).first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
