package com.summer.notifai.ui.contactlist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.summer.core.android.sms.constants.Constants.CONTACT_LIST_PAGE_SIZE
import com.summer.core.usecase.GetContactListByImportanceUseCase
import com.summer.notifai.ui.datamodel.ContactMessageInfoDataModel
import com.summer.notifai.ui.datamodel.mapper.ContactInfoMapper.toContactMessageInfoDataModel
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
class SmsContactListViewModel @Inject constructor(
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
            config = PagingConfig(pageSize = CONTACT_LIST_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = {
                getContactListByImportanceUseCase.invoke(isImportant)
            }
        ).flow
            .map { pagingData ->
                pagingData.map { contact ->
                    contact.toContactMessageInfoDataModel()
                }
            }
            .cachedIn(viewModelScope)
    }
}