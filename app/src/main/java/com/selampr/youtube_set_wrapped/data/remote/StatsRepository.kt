package com.selampr.youtube_set_wrapped.data.remote

import com.selampr.youtube_set_wrapped.data.remote.model.StatsRequestDto
import com.selampr.youtube_set_wrapped.data.remote.model.StatsResponseDto
import javax.inject.Inject

class StatsRepository @Inject constructor(
    private val api: StatsApi
) {
    suspend fun sendStats(request: StatsRequestDto): StatsResponseDto {
        return api.sendStats(request)
    }
}
