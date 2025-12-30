package com.selampr.youtube_set_wrapped.data.remote.model

data class VideoResultDto(
    val url: String,
    val title: String,
    val durationSeconds: Long,
    val thumbnail: String?
)
