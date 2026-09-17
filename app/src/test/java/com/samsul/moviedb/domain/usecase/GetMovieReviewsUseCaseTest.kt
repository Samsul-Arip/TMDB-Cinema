package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieReviewsUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieReviewsUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMovieReviewsUseCase(repository)
    }

    @Test
    fun `invoke returns movie reviews`() = runTest {
        val result = useCase(movieId = 1, page = 1).first()
        assertTrue(result is Resource.Success)
        val reviews = (result as Resource.Success).data
        assertEquals(1, reviews?.size)
        assertEquals("Critic", reviews?.first()?.author)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase(movieId = 1, page = 1).first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
