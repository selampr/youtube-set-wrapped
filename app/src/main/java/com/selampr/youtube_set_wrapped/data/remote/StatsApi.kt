package com.selampr.youtube_set_wrapped.data.remote

import com.selampr.youtube_set_wrapped.data.remote.model.StatsRequestDto
import com.selampr.youtube_set_wrapped.data.remote.model.StatsResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface StatsApi {

    @POST("/stats")
    suspend fun sendStats(
        @Body request: StatsRequestDto
    ): StatsResponseDto
}
