package com.samsul.moviedb.data.mapper

import com.samsul.moviedb.data.remote.dto.AuthorDetailsDto
import com.samsul.moviedb.data.remote.dto.GenreDto
import com.samsul.moviedb.data.remote.dto.MovieDetailDto
import com.samsul.moviedb.data.remote.dto.MovieDto
import com.samsul.moviedb.data.remote.dto.ReviewDto
import com.samsul.moviedb.data.remote.dto.VideoDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    @Test
    fun `genre mappings convert correctly between dto, entity, and domain`() {
        val dto = GenreDto(id = 28, name = "Action")

        val entity = dto.toEntity()
        assertEquals(28, entity.id)
        assertEquals("Action", entity.name)

        val domainFromEntity = entity.toDomain()
        assertEquals(28, domainFromEntity.id)
        assertEquals("Action", domainFromEntity.name)

        val domainFromDto = dto.toDomain()
        assertEquals(28, domainFromDto.id)
        assertEquals("Action", domainFromDto.name)
    }

    @Test
    fun `movie mappings convert correctly between dto, entity, and domain`() {
        val dto = MovieDto(
            id = 101,
            title = "Inception",
            overview = "A thief who steals corporate secrets...",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2010-07-16",
            voteAverage = 8.8,
            voteCount = 35000,
            genreIds = listOf(28, 878)
        )

        val entity = dto.toEntity(genreId = 28, page = 1, categoryType = "discover")
        assertEquals(101, entity.id)
        assertEquals(28, entity.genreId)
        assertEquals("Inception", entity.title)
        assertEquals(1, entity.page)

        val domainFromEntity = entity.toDomain()
        assertEquals(101, domainFromEntity.id)
        assertEquals("Inception", domainFromEntity.title)
        assertEquals(listOf(28), domainFromEntity.genreIds)

        val domainFromDto = dto.toDomain()
        assertEquals(101, domainFromDto.id)
        assertEquals("Inception", domainFromDto.title)
        assertEquals(listOf(28, 878), domainFromDto.genreIds)
    }

    @Test
    fun `movie detail mappings convert correctly between dto, entity, and domain`() {
        val detailDto = MovieDetailDto(
            id = 201,
            title = "Interstellar",
            overview = "A team of explorers travel through a wormhole...",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg",
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            voteCount = 32000,
            runtime = 169,
            status = "Released",
            genres = listOf(GenreDto(878, "Science Fiction"), GenreDto(18, "Drama"))
        )

        val entity = detailDto.toEntity()
        assertEquals(201, entity.id)
        assertEquals("Interstellar", entity.title)
        assertEquals(169, entity.runtime)
        assertNotNull(entity.genresJson)

        val domainFromEntity = entity.toDomain()
        assertEquals(201, domainFromEntity.id)
        assertEquals("Interstellar", domainFromEntity.title)
        assertEquals(2, domainFromEntity.genres.size)
        assertEquals("Science Fiction", domainFromEntity.genres[0].name)

        val domainFromDto = detailDto.toDomain()
        assertEquals(201, domainFromDto.id)
        assertEquals("Interstellar", domainFromDto.title)
        assertEquals(2, domainFromDto.genres.size)
    }

    @Test
    fun `review mappings convert correctly between dto, entity, and domain`() {
        val reviewDto = ReviewDto(
            id = "rev-001",
            author = "MovieBuff",
            content = "Masterpiece cinema.",
            createdAt = "2024-01-15",
            authorDetails = AuthorDetailsDto(
                name = "Buff",
                username = "moviebuff",
                avatarPath = "/avatar.png",
                rating = 9.5
            )
        )

        val entity = reviewDto.toEntity(movieId = 201, page = 1)
        assertEquals("rev-001", entity.id)
        assertEquals(201, entity.movieId)
        assertEquals("MovieBuff", entity.author)
        assertEquals("/avatar.png", entity.avatarPath)
        assertEquals(9.5, entity.rating)

        val domainFromEntity = entity.toDomain()
        assertEquals("rev-001", domainFromEntity.id)
        assertEquals("MovieBuff", domainFromEntity.author)
        assertEquals("Masterpiece cinema.", domainFromEntity.content)

        val domainFromDto = reviewDto.toDomain()
        assertEquals("rev-001", domainFromDto.id)
        assertEquals("MovieBuff", domainFromDto.author)
        assertEquals(9.5, domainFromDto.rating)
    }

    @Test
    fun `video mapping converts videoDto to domain trailer`() {
        val videoDto = VideoDto(
            id = "vid-123",
            key = "dQw4w9WgXcQ",
            name = "Official Teaser Trailer",
            site = "YouTube",
            type = "Trailer",
            official = true
        )

        val domain = videoDto.toDomain()
        assertEquals("vid-123", domain.id)
        assertEquals("dQw4w9WgXcQ", domain.key)
        assertEquals("Official Teaser Trailer", domain.name)
        assertEquals("YouTube", domain.site)
        assertEquals("Trailer", domain.type)
        assertTrue(domain.isOfficial)
    }
}
