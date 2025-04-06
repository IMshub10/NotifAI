package com.summer.core.android.sms.util

import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import androidx.core.net.toUri
import com.summer.core.android.sms.data.model.SmsInfoModel

object SmsUtils {

    fun extractSmsFromIntent(intent: Intent): SmsInfoModel? {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) return null

        val address = messages[0].originatingAddress
        val timestamp = messages[0].timestampMillis
        val serviceCenter = messages[0].serviceCenterAddress
        val subscriptionId = intent.getIntExtra("subscription", -1).takeIf { it != -1 }

        val fullBody = messages.joinToString(separator = "") { it.messageBody }

        return SmsInfoModel(
            address = address,
            body = fullBody,
            timestamp = timestamp,
            serviceCenter = serviceCenter,
            subscriptionId = subscriptionId
        )
    }

    suspend fun getOrCreateThreadId(context: Context, address: String): Long? {
        return try {
            val uri = "content://mms-sms/threadID".toUri()
            val queryUri = uri.buildUpon()
                .appendQueryParameter("recipient", address)
                .build()

            context.contentResolver.query(queryUri, arrayOf("_id"), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else null
            }
        } catch (e: Exception) {
            Log.e("SmsUtils", "Failed to get thread ID", e)
            null
        }
    }
}