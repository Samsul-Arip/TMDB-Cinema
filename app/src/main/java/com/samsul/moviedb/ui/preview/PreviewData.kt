package com.samsul.moviedb.ui.preview

import com.samsul.moviedb.core.util.Constants
import com.samsul.moviedb.domain.model.Genre
import com.samsul.moviedb.domain.model.Movie
import com.samsul.moviedb.domain.model.MovieDetail
import com.samsul.moviedb.domain.model.Review
import com.samsul.moviedb.domain.model.Trailer

/**
 * Compile-time constants for Preview names, mock text values, and identifiers.
 */
object PreviewConstants {
    // Screen Preview Names
    const val PREVIEW_GENRE_SUCCESS = "Genre Screen - Success"
    const val PREVIEW_GENRE_LOADING = "Genre Screen - Loading"
    const val PREVIEW_GENRE_SEARCH_ACTIVE = "Genre Screen - Search Active"
    const val PREVIEW_GENRE_EMPTY = "Genre Screen - Empty"
    const val PREVIEW_CATEGORY_CHIP = "Category Chip Preview"

    const val PREVIEW_ALL_GENRES_SUCCESS = "All Genres Screen - Success"
    const val PREVIEW_ALL_GENRES_LOADING = "All Genres Screen - Loading"
    const val PREVIEW_ALL_GENRES_EMPTY = "All Genres Screen - Empty"
    const val PREVIEW_GENRE_CARD = "Genre Category Card Preview"

    const val PREVIEW_MOVIE_LIST_SUCCESS = "Movie List Screen - Success"
    const val PREVIEW_MOVIE_LIST_LOADING = "Movie List Screen - Loading"
    const val PREVIEW_MOVIE_LIST_EMPTY = "Movie List Screen - Empty"

    const val PREVIEW_MOVIE_DETAIL_SUCCESS = "Movie Detail Screen - Success"
    const val PREVIEW_MOVIE_DETAIL_LOADING = "Movie Detail Screen - Loading"
    const val PREVIEW_MOVIE_DETAIL_ERROR = "Movie Detail Screen - Error"
    const val PREVIEW_REVIEW_CARD = "Review Card Preview"

    // Component Preview Names
    const val PREVIEW_CINEMA_MOVIE_CARD = "Cinema Movie Card Preview"
    const val PREVIEW_MOVIE_POSTER_CARD = "Movie Poster Card Preview"
    const val PREVIEW_YOUTUBE_PLAYER = "YouTube Player Placeholder Preview"
    const val PREVIEW_EMPTY_STATE = "Empty State Preview"
    const val PREVIEW_ERROR_STATE = "Error State Preview"
    const val PREVIEW_OFFLINE_BADGE = "Offline Badge Preview"
    const val PREVIEW_CATEGORY_ROW_SHIMMER = "Category Row Shimmer Preview"
    const val PREVIEW_MOVIE_GRID_SHIMMER = "Movie Grid Shimmer Preview"
    const val PREVIEW_GENRE_LIST_SHIMMER = "Genre List Shimmer Preview"

    // Mock String Values
    const val MOCK_GENRE_ACTION = "Action"
    const val MOCK_GENRE_ADVENTURE = "Adventure"
    const val MOCK_GENRE_ANIMATION = "Animation"
    const val MOCK_GENRE_COMEDY = "Comedy"
    const val MOCK_GENRE_CRIME = "Crime"
    const val MOCK_GENRE_DOCUMENTARY = "Documentary"
    const val MOCK_GENRE_HORROR = "Horror"
    const val MOCK_GENRE_SCIFI = "Science Fiction"

    const val MOCK_MOVIE_TITLE_1 = "Toy Story 5"
    const val MOCK_MOVIE_OVERVIEW_1 = "Woody and Buzz return for a brand new adventure."
    const val MOCK_MOVIE_TITLE_2 = "Resident Evil"
    const val MOCK_MOVIE_OVERVIEW_2 = "Survival action and horror awaits inside the facility."
    const val MOCK_MOVIE_TITLE_3 = "Colony"
    const val MOCK_MOVIE_OVERVIEW_3 = "Professor Se-jeong is thrust into a bloody nightmare when a rapidly mutating virus is released during a biotech conference."
    const val MOCK_MOVIE_TITLE_4 = "The Odyssey"
    const val MOCK_MOVIE_OVERVIEW_4 = "Epic mythological journey in ancient Greece."

    const val MOCK_POSTER_PATH_1 = "/poster1.jpg"
    const val MOCK_POSTER_PATH_2 = "/poster2.jpg"
    const val MOCK_POSTER_PATH_3 = "/poster3.jpg"
    const val MOCK_POSTER_PATH_4 = "/poster4.jpg"

    const val MOCK_BACKDROP_PATH_1 = "/backdrop1.jpg"
    const val MOCK_BACKDROP_PATH_2 = "/backdrop2.jpg"
    const val MOCK_BACKDROP_PATH_3 = "/backdrop3.jpg"
    const val MOCK_BACKDROP_PATH_4 = "/backdrop4.jpg"

    const val MOCK_RELEASE_DATE_1 = "2026-06-19"
    const val MOCK_RELEASE_DATE_2 = "2026-09-01"
    const val MOCK_RELEASE_DATE_3 = "2026-04-12"
    const val MOCK_RELEASE_DATE_4 = "2026-11-20"

    const val MOCK_STATUS_RELEASED = "Released"
    const val MOCK_SEARCH_QUERY = "Toy"

    const val MOCK_TRAILER_ID = "t1"
    const val MOCK_TRAILER_KEY = "dQw4w9WgXcQ"
    const val MOCK_TRAILER_NAME = "Official Trailer"

    const val MOCK_REVIEW_ID_1 = "r1"
    const val MOCK_REVIEW_AUTHOR_1 = "Leno"
    const val MOCK_REVIEW_CONTENT_1 = "Great visual effects and intense atmosphere throughout the whole runtime. Highly recommended for fans of the genre!"
    const val MOCK_REVIEW_DATE_1 = "2026-09-02"

    const val MOCK_REVIEW_ID_2 = "r2"
    const val MOCK_REVIEW_AUTHOR_2 = "Sarah"
    const val MOCK_REVIEW_CONTENT_2 = "A gripping survival thriller with stellar performances."
    const val MOCK_REVIEW_DATE_2 = "2026-09-05"

    const val MOCK_GENRE_CHIP_ALL = "All"
}

/**
 * Reusable mock data sets for Compose Previews across all screens.
 */
object PreviewData {
    val genres = listOf(
        Genre(28, PreviewConstants.MOCK_GENRE_ACTION),
        Genre(12, PreviewConstants.MOCK_GENRE_ADVENTURE),
        Genre(16, PreviewConstants.MOCK_GENRE_ANIMATION),
        Genre(35, PreviewConstants.MOCK_GENRE_COMEDY),
        Genre(80, PreviewConstants.MOCK_GENRE_CRIME),
        Genre(99, PreviewConstants.MOCK_GENRE_DOCUMENTARY)
    )

    val movies = listOf(
        Movie(
            id = 1,
            title = PreviewConstants.MOCK_MOVIE_TITLE_1,
            overview = PreviewConstants.MOCK_MOVIE_OVERVIEW_1,
            posterPath = PreviewConstants.MOCK_POSTER_PATH_1,
            backdropPath = PreviewConstants.MOCK_BACKDROP_PATH_1,
            releaseDate = PreviewConstants.MOCK_RELEASE_DATE_1,
            voteAverage = 8.4,
            voteCount = 500,
            genreIds = listOf(16)
        ),
        Movie(
            id = 2,
            title = PreviewConstants.MOCK_MOVIE_TITLE_2,
            overview = PreviewConstants.MOCK_MOVIE_OVERVIEW_2,
            posterPath = PreviewConstants.MOCK_POSTER_PATH_2,
            backdropPath = PreviewConstants.MOCK_BACKDROP_PATH_2,
            releaseDate = PreviewConstants.MOCK_RELEASE_DATE_2,
            voteAverage = 8.1,
            voteCount = 400,
            genreIds = listOf(28)
        ),
        Movie(
            id = 3,
            title = PreviewConstants.MOCK_MOVIE_TITLE_3,
            overview = PreviewConstants.MOCK_MOVIE_OVERVIEW_3,
            posterPath = PreviewConstants.MOCK_POSTER_PATH_3,
            backdropPath = PreviewConstants.MOCK_BACKDROP_PATH_3,
            releaseDate = PreviewConstants.MOCK_RELEASE_DATE_3,
            voteAverage = 8.1,
            voteCount = 300,
            genreIds = listOf(878)
        ),
        Movie(
            id = 4,
            title = PreviewConstants.MOCK_MOVIE_TITLE_4,
            overview = PreviewConstants.MOCK_MOVIE_OVERVIEW_4,
            posterPath = PreviewConstants.MOCK_POSTER_PATH_4,
            backdropPath = PreviewConstants.MOCK_BACKDROP_PATH_4,
            releaseDate = PreviewConstants.MOCK_RELEASE_DATE_4,
            voteAverage = 8.0,
            voteCount = 200,
            genreIds = listOf(12)
        )
    )

    val movieDetail = MovieDetail(
        id = 1,
        title = PreviewConstants.MOCK_MOVIE_TITLE_3,
        overview = PreviewConstants.MOCK_MOVIE_OVERVIEW_3,
        posterPath = PreviewConstants.MOCK_POSTER_PATH_3,
        backdropPath = PreviewConstants.MOCK_BACKDROP_PATH_3,
        releaseDate = PreviewConstants.MOCK_RELEASE_DATE_3,
        voteAverage = 8.1,
        voteCount = 420,
        runtime = 123,
        status = PreviewConstants.MOCK_STATUS_RELEASED,
        genres = listOf(
            Genre(28, PreviewConstants.MOCK_GENRE_ACTION),
            Genre(27, PreviewConstants.MOCK_GENRE_HORROR),
            Genre(878, PreviewConstants.MOCK_GENRE_SCIFI)
        )
    )

    val trailers = listOf(
        Trailer(
            id = PreviewConstants.MOCK_TRAILER_ID,
            key = PreviewConstants.MOCK_TRAILER_KEY,
            name = PreviewConstants.MOCK_TRAILER_NAME,
            site = Constants.VIDEO_SITE_YOUTUBE,
            type = Constants.VIDEO_TYPE_TRAILER,
            isOfficial = true
        )
    )

    val reviews = listOf(
        Review(
            id = PreviewConstants.MOCK_REVIEW_ID_1,
            author = PreviewConstants.MOCK_REVIEW_AUTHOR_1,
            content = PreviewConstants.MOCK_REVIEW_CONTENT_1,
            createdAt = PreviewConstants.MOCK_REVIEW_DATE_1,
            avatarPath = null,
            rating = 9.0
        ),
        Review(
            id = PreviewConstants.MOCK_REVIEW_ID_2,
            author = PreviewConstants.MOCK_REVIEW_AUTHOR_2,
            content = PreviewConstants.MOCK_REVIEW_CONTENT_2,
            createdAt = PreviewConstants.MOCK_REVIEW_DATE_2,
            avatarPath = null,
            rating = 8.5
        )
    )
}
