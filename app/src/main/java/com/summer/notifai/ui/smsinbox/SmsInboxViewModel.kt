package com.summer.notifai.ui.smsinbox

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.summer.core.usecase.GetContactListByImportanceUseCase
import com.summer.core.util.formatEpoch
import com.summer.notifai.R
import com.summer.notifai.ui.datamodel.ContactMessageInfoDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SmsInboxViewModel @Inject constructor(
    private val getContactListByImportanceUseCase: GetContactListByImportanceUseCase
) : ViewModel() {

    // Filter toggle state
    val isImportant = MutableLiveData(true)

    private val selectedImportance: StateFlow<Boolean> = isImportant.asFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, isImportant.value ?: true)

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedContacts: StateFlow<PagingData<ContactMessageInfoDataModel>> = selectedImportance
        .flatMapLatest { filter -> getPagedContacts(filter) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private fun getPagedContacts(isImportant: Boolean): Flow<PagingData<ContactMessageInfoDataModel>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                getContactListByImportanceUseCase.invoke(isImportant)
            }
        ).flow
            .map { pagingData ->
                pagingData.map { contact ->
                    ContactMessageInfoDataModel(
                        icon = R.drawable.ic_sms_24x24,
                        senderName = contact.senderName,
                        rawAddress = contact.rawAddress,
                        lastMessage = contact.lastMessage,
                        lastMessageDate = formatEpoch(contact.lastMessageDate),
                        unreadCount = contact.unreadCount.takeIf { it != 0 }?.toString()
                    )
                }
            }
            .cachedIn(viewModelScope)
    }
}