package com.samsul.moviedb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samsul.moviedb.core.util.Constants

@Entity(
    tableName = "movies",
    primaryKeys = ["id", "categoryType", "genreId"]
)
data class MovieEntity(
    val id: Int,
    val genreId: Int,
    val categoryType: String = Constants.CATEGORY_DISCOVER,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val page: Int
)
