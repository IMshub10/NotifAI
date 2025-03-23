package com.summer.core.di

import com.summer.core.repository.IOnboardingRepository
import com.summer.core.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepository: OnboardingRepository
    ): IOnboardingRepository
}