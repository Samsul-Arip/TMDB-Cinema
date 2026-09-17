package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieDetailUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMovieDetailUseCase(repository)
    }

    @Test
    fun `invoke returns movie detail`() = runTest {
        val result = useCase(movieId = 1).first()
        assertTrue(result is Resource.Success)
        val detail = (result as Resource.Success).data
        assertEquals("Test Movie Detail", detail?.title)
        assertEquals(120, detail?.runtime)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase(movieId = 1).first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
