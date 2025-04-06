package com.summer.notifai.ui.inbox

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.summer.core.android.sms.constants.Constants.SMS_LIST_PAGE_SIZE
import com.summer.core.data.local.model.ContactInfoInboxModel
import com.summer.core.ui.SmsImportanceType
import com.summer.core.domain.usecase.GetContactInfoInboxModelUseCase
import com.summer.core.domain.usecase.GetSmsMessagesBySenderIdUseCase
import com.summer.core.domain.usecase.MarkSmsAsReadForSenderUseCase
import com.summer.core.util.DateUtils
import com.summer.notifai.ui.datamodel.SmsInboxListItem
import com.summer.notifai.ui.datamodel.SmsMessageHeaderModel
import com.summer.notifai.ui.datamodel.mapper.SmsMessageMapper.toSmsMessageDataModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmsInboxViewModel @Inject constructor(
    private val getSmsMessagesBySenderIdUseCase: GetSmsMessagesBySenderIdUseCase,
    private val getContactInfoInboxModelUseCase: GetContactInfoInboxModelUseCase,
    private val markSmsAsReadForSenderUseCase: MarkSmsAsReadForSenderUseCase
) : ViewModel() {

    private val _contactInfoModel = MutableLiveData<ContactInfoInboxModel?>(null)
    val contactInfoModel = _contactInfoModel

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedSmsData: StateFlow<PagingData<SmsInboxListItem>> =
        _contactInfoModel.asFlow()
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { model ->
                getPagedSmsBySenderAddressId(
                    model.senderAddressId,
                    model.smsImportanceType ?: SmsImportanceType.ALL
                )
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    fun setContactInfoModel(senderAddressId: Long, smsImportanceType: SmsImportanceType) {
        getContactInfoInboxModelUseCase
            .invoke(senderAddressId, smsImportanceType.value)
            .onEach { contact ->
                _contactInfoModel.value = contact?.apply {
                    this.smsImportanceType = smsImportanceType
                }
            }.launchIn(viewModelScope)
    }

    private fun getPagedSmsBySenderAddressId(
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ): Flow<PagingData<SmsInboxListItem>> {
        return Pager(
            config = PagingConfig(pageSize = SMS_LIST_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                getSmsMessagesBySenderIdUseCase.invoke(senderAddressId, smsImportanceType)
            }
        ).flow
            .map { pagingData ->
                pagingData
                    .map { SmsInboxListItem.Message(it.toSmsMessageDataModel()) }
                    .insertSeparators { before, after ->
                        val beforeDate = before?.data?.dateInEpoch
                        val afterDate = after?.data?.dateInEpoch

                        val beforeDay = beforeDate?.let { DateUtils.formatDayHeader(it) }
                        val afterDay = afterDate?.let { DateUtils.formatDayHeader(it) }

                        // Insert header when date changes
                        if (beforeDay != null && afterDay != null && beforeDay != afterDay) {
                            return@insertSeparators SmsInboxListItem.Header(
                                SmsMessageHeaderModel(beforeDay)
                            )
                        }
                        null
                    }
            }
            .cachedIn(viewModelScope)
    }

    fun markSmsListAsRead(context: Context, senderAddressId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            markSmsAsReadForSenderUseCase.invoke(context, senderAddressId = senderAddressId)
        }
    }
}