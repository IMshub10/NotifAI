package com.summer.notifai.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.summer.core.android.notification.Constants.REQUEST_CODE_SUMMARY_NOTIFICATION
import com.summer.core.android.notification.NotificationIntentProvider
import com.summer.core.ui.model.SmsImportanceType
import com.summer.notifai.ui.home.SmsContactListActivity
import com.summer.notifai.ui.inbox.SmsInboxActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationIntentProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : NotificationIntentProvider {
    override fun provideSummaryPendingIntent(): PendingIntent {
        //TODO()
        val intent = Intent(context, SmsContactListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            REQUEST_CODE_SUMMARY_NOTIFICATION,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun provideSmsInboxPendingIntent(
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ): PendingIntent {
        val intent = SmsInboxActivity.onNewInstance(
            context,
            senderAddressId,
            smsImportanceType
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        return PendingIntent.getActivity(
            context,
            senderAddressId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}