package com.selampr.youtube_set_wrapped.data.remote.model

data class StatsResponseDto(
    val results: List<VideoResultDto>,
    val totalDurationMinutes: Long,
    val aiSummary: String
)
