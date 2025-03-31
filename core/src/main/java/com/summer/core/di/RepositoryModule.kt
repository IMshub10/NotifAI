package com.summer.core.di

import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.data.local.dao.ContactDao
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.repository.ContactRepository
import com.summer.core.repository.IContactRepository
import com.summer.core.repository.IOnboardingRepository
import com.summer.core.repository.ISmsRepository
import com.summer.core.repository.OnboardingRepository
import com.summer.core.repository.SmsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
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