package com.summer.core.di

import android.content.Context
import com.summer.core.data.local.dao.SMSDao
import com.summer.core.data.local.db.SMSDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SMSDatabase {
        return SMSDatabase.getDatabase(context)
    }

    @Provides
    fun provideSmsDao(database: SMSDatabase): SMSDao {
        return database.smsDao()
    }
}