// app/di/NotificationModule.kt
package com.summer.notifai.di

import com.summer.core.android.notification.NotificationIntentProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationIntentProvider(
        impl: NotificationIntentProviderImpl
    ): NotificationIntentProvider
}