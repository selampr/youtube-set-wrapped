package com.selampr.youtube_set_wrapped.util

import android.net.Uri

object YoutubeThumbnail {

    fun thumbnailUrl(videoUrl: String): String? {
        val videoId = extractVideoId(videoUrl) ?: return null
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    fun extractVideoId(videoUrl: String): String? {
        val uri = runCatching { Uri.parse(videoUrl) }.getOrNull() ?: return null
        val host = uri.host?.lowercase() ?: return null

        if (host.contains("youtu.be")) {
            return uri.pathSegments.firstOrNull()?.takeIf { it.isNotBlank() }
        }

        if (host.contains("youtube.com") || host.contains("music.youtube.com")) {
            uri.getQueryParameter("v")?.takeIf { it.isNotBlank() }?.let { return it }

            val segments = uri.pathSegments
            if (segments.isEmpty()) return null

            return when (segments[0]) {
                "shorts", "embed", "live" -> segments.getOrNull(1)
                else -> null
            }?.takeIf { it.isNotBlank() }
        }

        return null
    }
}
