package com.summer.core.android.sms.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.repository.SmsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsProcessingService : Service() {

    @Inject
    lateinit var repository: SmsRepository

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                createNotification("Starting SMS Processing..."),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, createNotification("Starting SMS Processing..."))
        }
        startProcessing()
    }

    private fun startProcessing() {
        serviceScope.launch {
            repository.fetchSMSFromDevice().collect { result ->
                when (result) {
                    is FetchResult.Loading -> {
                        updateNotification("Processing batch ${result.batchNumber} of ${result.totalBatches}...")
                        sendBroadcast(FetchResult.Loading(result.batchNumber, result.totalBatches))
                    }

                    is FetchResult.Success -> {
                        updateNotification("Processing completed successfully!")
                        sendBroadcast(FetchResult.Success)
                        repository.setSMSProcessingStatusCompleted(true)
                        stopSelf()
                    }

                    is FetchResult.Error -> {
                        updateNotification("Error: ${result.exception.message}")
                        sendBroadcast(FetchResult.Error(result.exception))
                        stopSelf()
                    }
                }
            }
        }
    }

    private fun sendBroadcast(fetchResult: FetchResult) {
        val intent = Intent(ACTION_SMS_PROCESSING_UPDATE)
        intent.putExtra(EXTRA_FETCH_RESULT, fetchResult)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun createNotification(contentText: String): Notification {
        val channelId = createNotificationChannel()

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("SMS Processing")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.star_on) //TODO(Replace with icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)  // 🔥 Ensures higher priority
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(false)
            .build()
    }

    private fun updateNotification(contentText: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(contentText)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(): String {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "SMS Processing",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
        return NOTIFICATION_CHANNEL_ID
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "sms_processing_channel"
        const val ACTION_SMS_PROCESSING_UPDATE =
            "com.summer.core.android.sms.ACTION_SMS_PROCESSING_UPDATE"
        const val EXTRA_FETCH_RESULT = "extra_fetch_result"

        fun startService(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {

                    Log.e("SmsProcessingService", "Permission not granted! Cannot start service.")
                    return
                }
            }

            val intent = Intent(context, SmsProcessingService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, SmsProcessingService::class.java)
            context.stopService(intent)
        }
    }
}