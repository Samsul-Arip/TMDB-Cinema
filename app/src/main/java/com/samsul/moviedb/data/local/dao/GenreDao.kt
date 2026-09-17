package com.samsul.moviedb.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.samsul.moviedb.data.local.entity.GenreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<GenreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM genres")
    suspend fun getGenreCount(): Int
}
