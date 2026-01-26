package com.selampr.youtube_set_wrapped.di

import com.selampr.youtube_set_wrapped.BuildConfig
import com.selampr.youtube_set_wrapped.data.remote.StatsApi
import com.selampr.youtube_set_wrapped.data.remote.StatsRepository
import com.selampr.youtube_set_wrapped.data.remote.YoutubeDataService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
        // Emulator/WSL reaches host machine's localhost via 10.0.2.2
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    @Singleton
    fun provideStatsApi(retrofit: Retrofit): StatsApi = retrofit.create(StatsApi::class.java)

    @Provides
    @Singleton
    fun provideStatsRepository(api: StatsApi): StatsRepository = StatsRepository(api)

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideYoutubeDataService(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): YoutubeDataService {
        return YoutubeDataService(BuildConfig.YOUTUBE_API_KEY, moshi, okHttpClient)
    }
}
