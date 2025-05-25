package com.summer.core.android.sms.processor

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import com.summer.core.android.sms.constants.SMSColumnNames
import com.summer.core.android.sms.util.SendSmsActions

/**
 * Utility class responsible for sending SMS messages using the system SmsManager.
 * Automatically prepares and attaches PendingIntents for sent and delivered tracking.
 */
object SmsSender {

    /**
     * Sends an SMS message with tracking for SENT and DELIVERED status.
     *
     * @param context The application context.
     * @param id Unique identifier for the SMS (usually a DB row ID).
     * @param address The destination phone number.
     * @param body The SMS message body.
     */
    fun sendSms(context: Context, id: Long, address: String, body: String) {
        val parts = getMessageParts(body)
        val sentIntent = createSentIntent(context, id, address)
        val deliveredIntent = createDeliveredIntent(context, id, address)

        val sentPendingIntent = createPendingIntent(context, id.toInt(), sentIntent)
        val deliveredPendingIntent = createPendingIntent(context, id.toInt(), deliveredIntent)

        val sentIntents = ArrayList<PendingIntent>().apply {
            repeat(parts.size) { add(sentPendingIntent) }
        }

        val deliveredIntents = ArrayList<PendingIntent>().apply {
            repeat(parts.size) { add(deliveredPendingIntent) }
        }

        getSmsManager().sendMultipartTextMessage(
            address,
            null,
            parts,
            sentIntents,
            deliveredIntents
        )
    }

    /**
     * Retrieves the SmsManager for the default subscription (SIM).
     *
     * @return The SmsManager instance.
     */
    private fun getSmsManager(): SmsManager {
        return SmsManager.getSmsManagerForSubscriptionId(SmsManager.getDefaultSmsSubscriptionId())
    }

    /**
     * Divides a long SMS message into parts suitable for multipart sending.
     *
     * @param body The original SMS message body.
     * @return A list of message parts.
     */
    private fun getMessageParts(body: String): ArrayList<String> {
        return getSmsManager().divideMessage(body) as ArrayList<String>
    }

    /**
     * Creates an intent for tracking SMS sent status.
     *
     * @param id SMS ID to include as extra.
     * @param address Recipient address.
     * @return Configured Intent for SMS_SENT broadcast.
     */
    private fun createSentIntent(
        context: Context,
        id: Long,
        address: String
    ): Intent {
        return Intent(SendSmsActions.ACTION_SMS_SENT.value).apply {
            setPackage(context.packageName)
            putExtra(SMSColumnNames.COLUMN_ID, id)
            putExtra(SMSColumnNames.COLUMN_ADDRESS, address)
        }
    }

    /**
     * Creates an intent for tracking SMS delivery status.
     *
     * @param context Context used to set app package.
     * @param id SMS ID to include as extra.
     * @param address Recipient address.
     * @return Configured Intent for SMS_DELIVERED broadcast.
     */
    private fun createDeliveredIntent(
        context: Context,
        id: Long,
        address: String
    ): Intent {
        return Intent(SendSmsActions.ACTION_SMS_DELIVERED.value).apply {
            setPackage(context.packageName)
            putExtra(SMSColumnNames.COLUMN_ID, id)
            putExtra(SMSColumnNames.COLUMN_ADDRESS, address)
        }
    }

    /**
     * Creates a PendingIntent for a given intent and request code.
     *
     * @param context Context to create the PendingIntent.
     * @param requestCode Unique code to differentiate intents.
     * @param intent The intent to wrap in a PendingIntent.
     * @return Configured immutable PendingIntent.
     */
    private fun createPendingIntent(
        context: Context,
        requestCode: Int,
        intent: Intent
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}