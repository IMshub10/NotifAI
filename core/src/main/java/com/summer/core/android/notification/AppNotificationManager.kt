package com.summer.core.android.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.summer.core.data.local.entities.SmsEntity
import com.summer.core.di.ChatSessionTracker
import com.summer.core.ui.model.SmsImportanceType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationIntentProvider: NotificationIntentProvider,
    private val chatSessionTracker: ChatSessionTracker
) {

    companion object {
        const val CHANNEL_CRITICAL = "channel_critical"
        const val CHANNEL_IMPORTANT = "channel_important"
        const val CHANNEL_GENERAL = "channel_general"
        const val CHANNEL_MINIMAL = "channel_minimal"
        const val CHANNEL_SUMMARY = "channel_summary"

        const val NOTIFICATION_ID_SUMMARY = 1001
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_CRITICAL,
                "Critical Messages",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Messages that require immediate attention with sound and popup."
            },

            NotificationChannel(
                CHANNEL_IMPORTANT,
                "Important Messages",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Messages with sound but no popup."
            },

            NotificationChannel(
                CHANNEL_GENERAL,
                "General Updates",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Silent messages shown in the notification drawer."
            },

            NotificationChannel(
                CHANNEL_MINIMAL,
                "Minimal Priority",
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = "No notification, hidden from drawer."
            },

            NotificationChannel(
                CHANNEL_SUMMARY,
                "Daily Summary",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Silent daily digest of messages."
                setSound(null, null)
                enableVibration(false)
            }
        )
        notificationManager.createNotificationChannels(channels)
    }

    fun showNotificationForSms(sms: SmsEntity) {
        if (sms.androidSmsId == null || chatSessionTracker.activeSenderAddressId == sms.senderAddressId) return
        val importance = sms.importanceScore ?: 3

        val (channelId, priority) = when (importance) {
            5 -> CHANNEL_CRITICAL to NotificationCompat.PRIORITY_HIGH
            4 -> CHANNEL_IMPORTANT to NotificationCompat.PRIORITY_DEFAULT
            3 -> CHANNEL_GENERAL to NotificationCompat.PRIORITY_LOW
            else -> return // Skip for importance 1 or 2
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.star_on) //TODO(Replace with icon)
            .setContentTitle("Message from ${sms.senderName ?: sms.rawAddress}")
            .setContentText(sms.body.take(60))
            .setContentIntent(
                notificationIntentProvider.provideSmsInboxPendingIntent(
                    senderAddressId = sms.senderAddressId,
                    smsImportanceType = SmsImportanceType.IMPORTANT
                )
            )
            .setPriority(priority)
            .setAutoCancel(true)
            .build()
        Log.d(
            "NotificationDebug",
            "Notifying for sms.androidSmsId=${sms.androidSmsId}, body=${sms.body.take(30)}"
        )
        notificationManager.notify(sms.androidSmsId.toInt(), notification)
    }

    fun clearNotificationForSender(androidSmsIds: List<Long>) {
        androidSmsIds.forEach {
            notificationManager.cancel(it.toInt())
        }
    }

    fun showDailySummaryNotification(totalMessages: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SUMMARY)
            .setSmallIcon(android.R.drawable.star_on) //TODO(Replace with icon)
            .setContentTitle("Today's Messages")
            .setContentText("You received $totalMessages messages today.")
            .setContentIntent(notificationIntentProvider.provideSummaryPendingIntent())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_SUMMARY, notification)
    }
}