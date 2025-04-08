package com.summer.core.di

import android.content.Context
import com.summer.core.android.permission.manager.IPermissionManager
import com.summer.core.android.permission.manager.PermissionManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {

    @Provides
    @Singleton
    fun providePermissionManager(@ApplicationContext context: Context): IPermissionManager {
        return PermissionManagerImpl(context)
    }
}