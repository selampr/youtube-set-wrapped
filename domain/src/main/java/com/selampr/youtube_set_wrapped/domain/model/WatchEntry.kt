package com.selampr.youtube_set_wrapped.domain.model

data class WatchEntry(
    val title: String,
    val url: String,
    val time: String?,
    val channel: String?
)
