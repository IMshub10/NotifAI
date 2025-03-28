package com.summer.core.di

import android.content.ContentResolver
import android.content.Context
import com.summer.core.android.sms.data.ISMSContentProvider
import com.summer.core.android.sms.data.SMSContentProvider
import com.summer.core.android.sms.processor.SmsBatchProcessor
import com.summer.core.data.local.dao.SMSDao
import com.summer.core.data.local.preference.SharedPreferencesManager
import com.summer.core.ml.model.SMSClassifierModel
import com.summer.core.repository.SmsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SMSModule {

    @Provides
    @Singleton
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Singleton
    fun provideSMSContentProvider(contentResolver: ContentResolver): ISMSContentProvider {
        return SMSContentProvider(contentResolver)
    }

    @Provides
    @Singleton
    fun provideSMSBatchProcessor(
        smsContentProvider: ISMSContentProvider,
        smsDao: SMSDao,
        smsClassifierModel: SMSClassifierModel
    ): SmsBatchProcessor {
        return SmsBatchProcessor(smsContentProvider, smsDao, smsClassifierModel)
    }

    @Provides
    @Singleton
    fun provideSmsRepository(
        batchProcessor: SmsBatchProcessor,
        sharedPreferencesManager: SharedPreferencesManager
    ): SmsRepository {
        return SmsRepository(batchProcessor, sharedPreferencesManager)
    }
}