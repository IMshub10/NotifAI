package com.summer.notifai.ui.inbox

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.LiveData
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
import com.summer.core.ui.model.SmsImportanceType
import com.summer.core.domain.usecase.GetContactInfoInboxModelUseCase
import com.summer.core.domain.usecase.GetSmsMessagesBySenderIdUseCase
import com.summer.core.domain.usecase.MarkSmsAsReadForSenderUseCase
import com.summer.core.domain.usecase.SendSmsUseCase
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
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.lifecycle.map
import com.summer.core.android.sms.constants.Constants
import com.summer.core.data.local.entities.SenderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

@HiltViewModel
class SmsInboxViewModel @Inject constructor(
    private val getSmsMessagesBySenderIdUseCase: GetSmsMessagesBySenderIdUseCase,
    private val getContactInfoInboxModelUseCase: GetContactInfoInboxModelUseCase,
    private val markSmsAsReadForSenderUseCase: MarkSmsAsReadForSenderUseCase,
    private val sendSmsUseCase: SendSmsUseCase
) : ViewModel() {

    val messageText = MutableLiveData("")

    val isSendEnabled: LiveData<Boolean> = messageText.map { message ->
        val parts = SmsManager.getDefault().divideMessage(message ?: "")
        !message.isNullOrBlank() && parts.size <= Constants.SMS_PART_LIMIT
    }

    private val _contactInfoModel = MutableLiveData<ContactInfoInboxModel?>(null)
    val contactInfoModel = _contactInfoModel

    private val _targetSmsId = MutableStateFlow<Long?>(null)

    val isSendSectionVisible: LiveData<Boolean> = contactInfoModel.map { model ->
        model?.senderType == SenderType.CONTACT
    }

    var senderAddressId = 0L

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagedSmsData: StateFlow<PagingData<SmsInboxListItem>> =
        combine(
            _contactInfoModel.asFlow().filterNotNull().distinctUntilChanged(),
            _targetSmsId
        ) { model, smsId ->
            Triple(model.senderAddressId, model.smsImportanceType ?: SmsImportanceType.ALL, smsId)
        }
            .filterNotNull()
            .distinctUntilChanged()
            .flatMapLatest { model ->
                getPagedSmsBySenderAddressId(
                    model.first,
                    model.second
                )
            }
            .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    fun setContactInfoModel(senderAddressId: Long, smsImportanceType: SmsImportanceType) {
        this.senderAddressId = senderAddressId
        getContactInfoInboxModelUseCase
            .invoke(senderAddressId, smsImportanceType.value)
            .onEach { contact ->
                _contactInfoModel.value = contact?.apply {
                    this.smsImportanceType = smsImportanceType
                }
            }.launchIn(viewModelScope)
    }

    fun setTargetSmsId(id: Long?) {
        _targetSmsId.value = id
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

    fun sendSms(context: Context, body: String?) {
        if (body.isNullOrBlank()) return
        val contact = _contactInfoModel.value

        if (contact == null || contact.phoneNumber.isNullOrEmpty()) {
            Log.e("SmsInbox", "No phone number found for senderAddressId=$senderAddressId")
            return
        }

        viewModelScope.launch {
            sendSmsUseCase.invoke(
                context,
                contact.senderAddressId,
                contact.phoneNumber.orEmpty(),
                body
            )
        }
    }

    fun markSmsListAsRead(
        context: Context,
        senderAddressId: Long,
        callback: (smsIds: List<Long>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.Default) {
            val smsIds =
                markSmsAsReadForSenderUseCase.invoke(context, senderAddressId = senderAddressId)
            withContext(Dispatchers.Main) {
                callback(smsIds)
            }
        }
    }
}