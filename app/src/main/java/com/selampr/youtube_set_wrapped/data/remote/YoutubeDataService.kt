package com.selampr.youtube_set_wrapped.data.remote

import com.squareup.moshi.Moshi
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class YoutubeDataService(
    private val apiKey: String,
    private val moshi: Moshi,
    private val client: OkHttpClient
) {

    fun isConfigured(): Boolean = apiKey.isNotBlank()

    suspend fun fetchDurationsSeconds(videoIds: List<String>): Map<String, Long> {
        require(isConfigured()) { "YouTube API key is missing." }

        val ids = videoIds.distinct().filter { it.isNotBlank() }
        if (ids.isEmpty()) return emptyMap()

        val adapter = moshi.adapter(YoutubeVideosResponse::class.java)
        val result = mutableMapOf<String, Long>()

        ids.chunked(50).forEach { chunk ->
            val url = "https://www.googleapis.com/youtube/v3/videos"
                .toHttpUrl()
                .newBuilder()
                .addQueryParameter("part", "contentDetails")
                .addQueryParameter("id", chunk.joinToString(","))
                .addQueryParameter("key", apiKey)
                .build()

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string().orEmpty()
                    throw IllegalStateException(
                        "YouTube API error: ${response.code}. $errorBody"
                    )
                }

                val body = response.body?.string().orEmpty()
                val parsed = adapter.fromJson(body) ?: return@use

                parsed.items.forEach { item ->
                    val seconds = parseDurationSeconds(item.contentDetails.duration)
                    result[item.id] = seconds
                }
            }
        }

        return result
    }

    suspend fun fetchVideoDetails(videoId: String): VideoDetails? {
        require(isConfigured()) { "YouTube API key is missing." }

        if (videoId.isBlank()) return null

        val adapter = moshi.adapter(YoutubeVideosResponse::class.java)
        val url = "https://www.googleapis.com/youtube/v3/videos"
            .toHttpUrl()
            .newBuilder()
            .addQueryParameter("part", "contentDetails,snippet")
            .addQueryParameter("id", videoId)
            .addQueryParameter("key", apiKey)
            .build()

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errorBody = response.body?.string().orEmpty()
                throw IllegalStateException(
                    "YouTube API error: ${response.code}. $errorBody"
                )
            }

            val body = response.body?.string().orEmpty()
            val parsed = adapter.fromJson(body) ?: return null
            val item = parsed.items.firstOrNull() ?: return null
            val seconds = parseDurationSeconds(item.contentDetails.duration)
            return VideoDetails(
                id = item.id,
                title = item.snippet.title,
                channelTitle = item.snippet.channelTitle,
                durationSeconds = seconds
            )
        }
    }

    private fun parseDurationSeconds(isoDuration: String): Long {
        val regex = Regex("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?")
        val match = regex.matchEntire(isoDuration) ?: return 0L
        val hours = match.groupValues[1].toLongOrNull() ?: 0L
        val minutes = match.groupValues[2].toLongOrNull() ?: 0L
        val seconds = match.groupValues[3].toLongOrNull() ?: 0L
        return (hours * 3600L) + (minutes * 60L) + seconds
    }

    data class YoutubeVideosResponse(
        val items: List<YoutubeVideoItem> = emptyList()
    )

    data class YoutubeVideoItem(
        val id: String = "",
        val contentDetails: YoutubeContentDetails = YoutubeContentDetails(),
        val snippet: YoutubeSnippet = YoutubeSnippet()
    )

    data class YoutubeContentDetails(
        val duration: String = ""
    )

    data class YoutubeSnippet(
        val title: String = "",
        val channelTitle: String = ""
    )

    data class VideoDetails(
        val id: String,
        val title: String,
        val channelTitle: String,
        val durationSeconds: Long
    )
}
