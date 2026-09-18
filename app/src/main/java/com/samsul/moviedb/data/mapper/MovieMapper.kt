package com.samsul.moviedb.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.samsul.moviedb.core.util.Constants
import com.samsul.moviedb.data.local.entity.GenreEntity
import com.samsul.moviedb.data.local.entity.MovieDetailEntity
import com.samsul.moviedb.data.local.entity.MovieEntity
import com.samsul.moviedb.data.local.entity.ReviewEntity
import com.samsul.moviedb.data.remote.dto.GenreDto
import com.samsul.moviedb.data.remote.dto.MovieDetailDto
import com.samsul.moviedb.data.remote.dto.MovieDto
import com.samsul.moviedb.data.remote.dto.ReviewDto
import com.samsul.moviedb.data.remote.dto.VideoDto
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer

private val gson = Gson()

// Genre mappings
fun GenreDto.toEntity() = GenreEntity(id = id, name = name)
fun GenreEntity.toDomain() = Genre(id = id, name = name)
fun GenreDto.toDomain() = Genre(id = id, name = name)

// Movie mappings
fun MovieDto.toEntity(genreId: Int, page: Int, categoryType: String = Constants.CATEGORY_DISCOVER) = MovieEntity(
    id = id,
    genreId = genreId,
    categoryType = categoryType,
    title = title ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    page = page
)

fun MovieEntity.toDomain() = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    genreIds = listOf(genreId)
)

fun MovieDto.toDomain(genreId: Int = 0) = Movie(
    id = id,
    title = title ?: "",
    overview = overview ?: "",
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage ?: 0.0,
    voteCount = voteCount ?: 0,
    genreIds = genreIds ?: if (genreId != 0) listOf(genreId) else emptyList()
)

// MovieDetail mappings
fun MovieDetailDto.toEntity(): MovieDetailEntity {
    val genresList = genres?.map { it.toDomain() } ?: emptyList()
    return MovieDetailEntity(
        id = id,
        title = title ?: "",
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtime = runtime,
        status = status,
        genresJson = gson.toJson(genresList)
    )
}

fun MovieDetailEntity.toDomain(): MovieDetail {
    val genreType = object : TypeToken<List<Genre>>() {}.type
    val parsedGenres: List<Genre> = try {
        gson.fromJson(genresJson, genreType) ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
    return MovieDetail(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        status = status,
        genres = parsedGenres
    )
}

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = id,
        title = title ?: "",
        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0,
        runtime = runtime,
        status = status,
        genres = genres?.map { it.toDomain() } ?: emptyList()
    )
}

// Review mappings
fun ReviewDto.toEntity(movieId: Int, page: Int) = ReviewEntity(
    id = id,
    movieId = movieId,
    author = author ?: "Anonymous",
    content = content ?: "",
    createdAt = createdAt,
    avatarPath = authorDetails?.avatarPath,
    rating = authorDetails?.rating,
    page = page
)

fun ReviewEntity.toDomain() = Review(
    id = id,
    author = author,
    content = content,
    createdAt = createdAt,
    avatarPath = avatarPath,
    rating = rating
)

fun ReviewDto.toDomain() = Review(
    id = id,
    author = author ?: "Anonymous",
    content = content ?: "",
    createdAt = createdAt,
    avatarPath = authorDetails?.avatarPath,
    rating = authorDetails?.rating
)

// Video/Trailer mappings
fun VideoDto.toDomain() = Trailer(
    id = id,
    key = key,
    name = name ?: "",
    site = site ?: "",
    type = type ?: "",
    isOfficial = official ?: false
)
