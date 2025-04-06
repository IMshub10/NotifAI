package com.summer.core.di

import com.summer.core.data.repository.ContactRepository
import com.summer.core.domain.repository.IContactRepository
import com.summer.core.domain.repository.IOnboardingRepository
import com.summer.core.domain.repository.ISmsRepository
import com.summer.core.data.repository.OnboardingRepository
import com.summer.core.data.repository.SmsRepository
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

    @Binds
    @Singleton
    abstract fun bindSmsRepository(
        smsRepository: SmsRepository
    ): ISmsRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(
        contactRepository: ContactRepository
    ): IContactRepository

}