package com.omnistream.app.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface MediaItem {
    val id: String
    val title: String
    val overview: String
    val posterUrl: String
    val backdropUrl: String
    val rating: Float
}

@Serializable
data class Movie(
    override val id: String,
    override val title: String,
    override val overview: String,
    override val posterUrl: String,
    override val backdropUrl: String,
    override val rating: Float,
    val runtimeMinutes: Int
) : MediaItem

@Serializable
data class TvShow(
    override val id: String,
    override val title: String,
    override val overview: String,
    override val posterUrl: String,
    override val backdropUrl: String,
    override val rating: Float,
    val seasons: Int
) : MediaItem