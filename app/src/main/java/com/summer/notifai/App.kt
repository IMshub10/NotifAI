package com.summer.notifai

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.StrictMode
import android.provider.ContactsContract
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.airbnb.lottie.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.notification.AppNotificationManager
import com.summer.core.android.permission.manager.IPermissionManager
import com.summer.core.android.phone.service.ContactObserver
import com.summer.core.android.sms.receiver.SentSmsReceiver
import com.summer.core.android.sms.util.SendSmsActions
import com.summer.notifai.di.ContactObserverDepsEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appNotificationManager: AppNotificationManager

    @Inject
    lateinit var permissionManager: IPermissionManager

    private val smsSentReceiver = SentSmsReceiver()


    override fun onCreate() {
        super.onCreate()
        appNotificationManager.createNotificationChannels()
        setUpSyncContacts()
        setUpStrictMode()
        registerSentSmsReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerSentSmsReceiver() {
        val sentFilter = IntentFilter(SendSmsActions.ACTION_SMS_SENT.value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsSentReceiver, sentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(smsSentReceiver, sentFilter)
        }
    }

    private fun setUpStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )

            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    private fun setUpSyncContacts() {
        if (permissionManager.hasReadContacts()) {
            val observer = registerContactObserver(this)
            observer.onChange(true)
            ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    super.onStart(owner)
                    observer.syncContacts()
                }
            })
        }
    }

    private fun registerContactObserver(context: Context): ContactObserver {
        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            ContactObserverDepsEntryPoint::class.java
        )
        val observer = entryPoint.contactObserver()
        context.contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            observer
        )
        return observer
    }
}

