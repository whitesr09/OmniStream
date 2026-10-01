package com.omnistream.app.core.plugin

import kotlinx.coroutines.flow.Flow

interface MediaSourcePlugin {
    val id: String
    val name: String
    fun search(query: String): Flow<List<MediaSearchResult>>
    suspend fun getVideoLinks(mediaId: String): List<VideoLink>
}

data class MediaSearchResult(
    val pluginMediaId: String,
    val title: String,
    val posterUrl: String?,
    val type: String
)

data class VideoLink(
    val url: String,
    val quality: String,
    val headers: Map<String, String> = emptyMap()
)