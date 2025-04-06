package com.summer.notifai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.summer.core.android.permission.manager.IPermissionManager
import com.summer.core.android.permission.manager.IPermissionManagerImpl
import com.summer.core.di.SmsReceiverEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("SMSBroadCastReceiver", intent?.dataString.orEmpty())
        if (context == null || intent == null) return

        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val appContext = context.applicationContext
            val entryPoint = EntryPointAccessors.fromApplication(
                appContext,
                SmsReceiverEntryPoint::class.java
            )

            val smsInserter = entryPoint.smsInserter()
            val permissionManager: IPermissionManager = IPermissionManagerImpl(context = context)
            val appNotificationManager = entryPoint.appNotificationManager()

            CoroutineScope(Dispatchers.IO).launch {
                val sms = smsInserter.processIncomingSms(appContext, intent)
                if (permissionManager.hasSendNotifications() && sms != null) {
                    appNotificationManager.showNotificationForSms(sms = sms)
                }
            }
        }
    }
}