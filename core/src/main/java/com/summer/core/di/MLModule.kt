package com.summer.core.di

import android.content.Context
import com.summer.core.ml.model.SMSClassifierModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModelModule {

    @Provides
    @Singleton
    fun provideSMSClassifierModel(context: Context): SMSClassifierModel {
        return SMSClassifierModel(context)
    }
}