package com.summer.core.android.phone.service


import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.summer.core.android.phone.processor.ContactProcessor
import com.summer.core.data.local.entities.ContactEntity
import com.summer.core.di.ContactObserverEntryPoint
import com.summer.core.repository.IContactRepository
import com.summer.core.usecase.SyncContactsUseCase
import com.summer.core.util.ResultState
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactObserver
@Inject constructor(
    @ApplicationContext private val context: Context
) : ContentObserver(Handler(Looper.getMainLooper())) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        Log.d("ContactObserver", "Contacts changed at URI: $uri")

        syncContacts()
    }

    fun syncContacts() {
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(context, ContactObserverEntryPoint::class.java)
        val repository = hiltEntryPoint.contactRepository()

        val syncContactsUseCase = hiltEntryPoint.syncContactsUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            checkForRecentNameChanges(syncContactsUseCase = syncContactsUseCase, repository)
        }
    }

    private suspend fun checkForRecentNameChanges(
        syncContactsUseCase: SyncContactsUseCase,
        repository: IContactRepository
    ) {
        val lastCheckedTimestamp = repository.getPhoneTableLastUpdated()

        val contactProcessor = ContactProcessor<ContactEntity>(context)

        contactProcessor.fetchContacts(lastCheckedTimestamp).collect { result ->
            when (result) {
                is ResultState.InProgress -> Log.d(
                    "ContactSync",
                    "Fetching contacts in progress..."
                )

                is ResultState.Success -> {
                    val updatedContacts = result.data

                    if (updatedContacts.isNotEmpty()) {
                        syncContactsUseCase.execute(updatedContacts).collect { syncResult ->
                            when (syncResult) {
                                is ResultState.InProgress -> Log.d(
                                    "ContactSync",
                                    "Upserting contacts..."
                                )

                                is ResultState.Success -> {
                                    repository.setPhoneTableLastUpdated(System.currentTimeMillis())
                                }

                                is ResultState.Failed -> Log.e(
                                    "ContactSync",
                                    "Error upserting contacts",
                                    syncResult.error
                                )
                            }
                        }
                    }
                }

                is ResultState.Failed -> Log.e(
                    "ContactSync",
                    "Error fetching contacts",
                    result.error
                )
            }
        }
    }

}