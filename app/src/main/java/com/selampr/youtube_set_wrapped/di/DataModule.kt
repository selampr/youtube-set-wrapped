package com.selampr.youtube_set_wrapped.di

import com.selampr.youtube_set_wrapped.data.repository.HistoryRepositoryImpl
import com.selampr.youtube_set_wrapped.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepositoryImpl
    ): HistoryRepository
}
