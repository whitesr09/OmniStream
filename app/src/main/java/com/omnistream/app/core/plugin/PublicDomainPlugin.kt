package com.omnistream.app.core.plugin

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class PublicDomainPlugin @Inject constructor() : MediaSourcePlugin {
    override val id = "com.omnistream.plugin.public_domain"
    override val name = "Public Domain (Blender)"

    override fun search(query: String): Flow<List<MediaSearchResult>> = flow {
        delay(500)
        emit(listOf(
            MediaSearchResult("bbb", "Big Buck Bunny", "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg", "Movie"),
            MediaSearchResult("sintel", "Sintel", "https://durian.blender.org/wp-content/uploads/2010/06/sintel_poster.jpg", "Movie")
        ))
    }.flowOn(Dispatchers.IO)

    override suspend fun getVideoLinks(mediaId: String): List<VideoLink> {
        return when (mediaId) {
            "bbb" -> listOf(VideoLink("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "1080p"))
            "sintel" -> listOf(VideoLink("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4", "1080p"))
            else -> emptyList()
        }
    }
}