package com.summer.notifai

import android.app.Application
import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.summer.core.android.phone.processor.ContactProcessor
import com.summer.core.android.phone.service.ContactObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val observer = registerContactObserver(this)
        observer.onChange(true)


        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                observer.syncContacts()
            }
        })
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

