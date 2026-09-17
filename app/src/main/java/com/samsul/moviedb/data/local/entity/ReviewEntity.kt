package com.samsul.moviedb.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey
    val id: String,
    val movieId: Int,
    val author: String,
    val content: String,
    val createdAt: String?,
    val avatarPath: String?,
    val rating: Double?,
    val page: Int
)
