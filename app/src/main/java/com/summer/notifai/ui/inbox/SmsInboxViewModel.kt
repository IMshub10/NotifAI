package com.summer.notifai.ui.inbox

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.summer.core.android.sms.constants.Constants
import com.summer.core.android.sms.constants.Constants.SMS_LIST_PAGE_SIZE
import com.summer.core.data.local.entities.SenderType
import com.summer.core.data.local.model.ContactInfoInboxModel
import com.summer.core.domain.usecase.GetContactInfoInboxModelUseCase
import com.summer.core.domain.usecase.MarkSmsAsReadForSenderUseCase
import com.summer.core.domain.usecase.SendSmsUseCase
import com.summer.core.ui.model.SmsImportanceType
import com.summer.notifai.ui.inbox.smsMessages.SmsMessageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SmsInboxViewModel @Inject constructor(
    private val sendSmsUseCase: SendSmsUseCase,
    private val getContactInfoInboxModelUseCase: GetContactInfoInboxModelUseCase,
    private val markSmsAsReadForSenderUseCase: MarkSmsAsReadForSenderUseCase,
    private val smsMessageLoaderFactory: SmsMessageLoader.Factory
) : ViewModel() {

    var senderAddressId = 0L
    var isListInitScrollCalled = false
    var targetSmsId: Long? = null
    private var smsImportanceType: SmsImportanceType = SmsImportanceType.ALL

    val messageText = MutableLiveData("")

    val isSendEnabled: LiveData<Boolean> = messageText.map { message ->
        val parts = SmsManager.getDefault().divideMessage(message ?: "")
        !message.isNullOrBlank() && parts.size <= Constants.SMS_PART_LIMIT
    }

    val isAtBottom = MutableStateFlow(true)
    val isRecyclerViewScrolling = MutableStateFlow(false)

    val isScrollDownButtonVisible = combine(isAtBottom, isRecyclerViewScrolling) { atBottom, scrolling ->
        scrolling && !atBottom
    }.asLiveData()

    private val _contactInfoModel = MutableLiveData<ContactInfoInboxModel?>(null)
    val contactInfoModel: LiveData<ContactInfoInboxModel?> = _contactInfoModel

    val isSendSectionVisible: LiveData<Boolean> = contactInfoModel.map { model ->
        model?.senderType == SenderType.CONTACT
    }

    private val _highlightedMessageId = MutableStateFlow<Long?>(null)
    val highlightedMessageId: StateFlow<Long?> = _highlightedMessageId

    private val _messageLoader = MutableStateFlow<SmsMessageLoader?>(null)
    val messageLoader: StateFlow<SmsMessageLoader?> get() = _messageLoader.asStateFlow()

    fun flashMessage(id: Long, resetCallback: () -> Unit) {
        _highlightedMessageId.value = id
        Handler(Looper.getMainLooper()).postDelayed({
            _highlightedMessageId.value = null
            resetCallback()
        }, 800L)
    }

    fun setContactInfoModel(
        targetSmsId: Long?,
        senderAddressId: Long,
        smsImportanceType: SmsImportanceType
    ) {
        this.targetSmsId = targetSmsId
        this.senderAddressId = senderAddressId
        this.smsImportanceType = smsImportanceType
        getContactInfoInboxModelUseCase
            .invoke(senderAddressId, smsImportanceType.value)
            .onEach { contact ->
                _contactInfoModel.value = contact?.apply {
                    this.smsImportanceType = smsImportanceType
                }
            }.launchIn(viewModelScope)
        initSmsMessageLoader()
    }

    private fun initSmsMessageLoader() {
        val loader = smsMessageLoaderFactory.create(
            senderAddressId = senderAddressId,
            pageSize = SMS_LIST_PAGE_SIZE,
            scope = viewModelScope
        )
        _messageLoader.value = loader
        viewModelScope.launch { loader.reinitialize(smsImportanceType, targetSmsId) }
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
            val smsIds = markSmsAsReadForSenderUseCase.invoke(context, senderAddressId)
            withContext(Dispatchers.Main) {
                callback(smsIds)
            }
        }
    }
}
