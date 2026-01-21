package com.selampr.youtube_set_wrapped.di

import com.selampr.youtube_set_wrapped.BuildConfig
import com.selampr.youtube_set_wrapped.data.ai.OpenAiStatsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    @Provides
    @Singleton
    fun provideOpenAiStatsService(): OpenAiStatsService {
        return OpenAiStatsService(BuildConfig.OPENAI_API_KEY)
    }
}
