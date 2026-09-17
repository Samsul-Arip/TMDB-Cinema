package com.samsul.moviedb.domain.usecase

import com.samsul.moviedb.core.util.Resource
import com.samsul.moviedb.data.repository.FakeMovieRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetMovieGenresUseCaseTest {

    private lateinit var repository: FakeMovieRepository
    private lateinit var useCase: GetMovieGenresUseCase

    @Before
    fun setUp() {
        repository = FakeMovieRepository()
        useCase = GetMovieGenresUseCase(repository)
    }

    @Test
    fun `invoke returns success with genres list`() = runTest {
        val result = useCase().first()
        assertTrue(result is Resource.Success)
        assertEquals(2, (result as Resource.Success).data?.size)
        assertEquals("Action", result.data?.first()?.name)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        repository.shouldReturnError = true
        val result = useCase().first()
        assertTrue(result is Resource.Error)
        assertEquals("Network error", (result as Resource.Error).message)
    }
}
