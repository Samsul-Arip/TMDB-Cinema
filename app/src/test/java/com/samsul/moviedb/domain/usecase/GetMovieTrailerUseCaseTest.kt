package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieTrailerUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieTrailerUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMovieTrailerUseCase(repository)
    }

    @Test
    fun `invoke returns movie trailers`() = runTest {
        val result = useCase(movieId = 1).first()
        assertTrue(result is Resource.Success)
        val trailers = (result as Resource.Success).data
        assertEquals(1, trailers?.size)
        assertEquals("dQw4w9WgXcQ", trailers?.first()?.key)
        assertTrue(trailers?.first()?.isYouTube == true)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase(movieId = 1).first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
