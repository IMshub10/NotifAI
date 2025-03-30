package com.summer.core.di

import android.content.Context
import com.summer.core.util.CountryCodeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CountryProviderModule {
    @Provides
    @Singleton
    fun provideCountryCodeProvider(@ApplicationContext context: Context): CountryCodeProvider {
        return CountryCodeProvider(context)
    }
}