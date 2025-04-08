package com.summer.notifai

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.provider.ContactsContract
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.airbnb.lottie.BuildConfig
import com.summer.core.android.notification.AppNotificationManager
import com.summer.core.android.permission.manager.IPermissionManager
import com.summer.core.android.phone.service.ContactObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var appNotificationManager: AppNotificationManager

    @Inject
    lateinit var permissionManager: IPermissionManager

    override fun onCreate() {
        super.onCreate()
        appNotificationManager.createNotificationChannels()
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

    private fun registerContactObserver(context: Context): ContactObserver {
        val observer = ContactObserver(context)
        context.contentResolver.registerContentObserver(
            ContactsContract.Contacts.CONTENT_URI,
            true,
            observer
        )
        return observer
    }
}

