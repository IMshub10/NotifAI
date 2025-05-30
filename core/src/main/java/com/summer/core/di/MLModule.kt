package com.summer.core.di

import android.content.Context
import com.summer.core.ml.model.SmsClassifierModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Provides
    @Singleton
    fun provideSmsClassifierModel(@ApplicationContext context: Context): SmsClassifierModel {
        return SmsClassifierModel(context)
    }
}