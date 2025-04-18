package com.summer.notifai.di

import com.summer.core.data.local.dao.SmsDao
import com.summer.core.ui.model.SmsImportanceType
import com.summer.notifai.ui.inbox.smsMessages.SmsMessageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope

@Module
@dagger.hilt.InstallIn(SingletonComponent::class)
object SmsLoaderModule {

    @Provides
    fun provideSmsMessageLoaderFactory(
        smsDao: SmsDao
    ): SmsMessageLoader.Factory {
        return object : SmsMessageLoader.Factory {
            override fun create(
                senderAddressId: Long,
                pageSize: Int,
                scope: CoroutineScope
            ): SmsMessageLoader {
                return SmsMessageLoader(
                    smsDao = smsDao,
                    senderAddressId = senderAddressId,
                    pageSize = pageSize,
                    scope = scope
                )
            }
        }
    }
}