package com.samsul.moviedb.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.samsul.moviedb.data.local.dao.GenreDao
import com.samsul.moviedb.data.local.dao.MovieDao
import com.samsul.moviedb.data.local.dao.MovieDetailDao
import com.samsul.moviedb.data.local.dao.ReviewDao
import com.samsul.moviedb.data.local.entity.GenreEntity
import com.samsul.moviedb.data.local.entity.MovieDetailEntity
import com.samsul.moviedb.data.local.entity.MovieEntity
import com.samsul.moviedb.data.local.entity.ReviewEntity

@Database(
    entities = [
        GenreEntity::class,
        MovieEntity::class,
        MovieDetailEntity::class,
        ReviewEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun movieDao(): MovieDao
    abstract fun movieDetailDao(): MovieDetailDao
    abstract fun reviewDao(): ReviewDao
}
