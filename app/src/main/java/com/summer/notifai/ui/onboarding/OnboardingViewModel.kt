package com.summer.notifai.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.entities.ContactEntity
import com.summer.core.repository.IOnboardingRepository
import com.summer.core.usecase.SyncContactsUseCase
import com.summer.core.util.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: IOnboardingRepository,
    private val syncContactsUseCase: SyncContactsUseCase
) : ViewModel() {

    private val _fetchStatus = MutableLiveData<FetchResult>()
    val fetchStatus: LiveData<FetchResult> get() = _fetchStatus

    private val _contactsSync = MutableLiveData<ResultState<Int>>()
    val contactsSync: LiveData<FetchResult> get() = _fetchStatus

    private fun setUserAgreement() {
        viewModelScope.launch {
            onboardingRepository.setUserAgreement(true)
        }
    }

    private fun setDataSharing(enabled: Boolean) {
        viewModelScope.launch {
            onboardingRepository.setDataSharing(enabled)
        }
    }

    fun onOptionalDataSharingEnabled() {
        setUserAgreement()
        setDataSharing(true)
    }

    fun onOptionalDataSharingDisabled() {
        setUserAgreement()
        setDataSharing(false)
    }

    fun updateFetchResult(result: FetchResult) {
        _fetchStatus.postValue(result)
    }

    fun areContactsSynced(): Boolean {
        return onboardingRepository.areContactsSynced()
    }

    fun syncContacts(contacts: List<ContactEntity>) {
        syncContactsUseCase.execute(contacts).map {
            when (it) {
                is ResultState.Failed -> {
                    _contactsSync.postValue(it)
                }

                ResultState.InProgress -> {
                    _contactsSync.postValue(it)
                }

                is ResultState.Success<*> -> {
                    viewModelScope.launch {
                        _contactsSync.postValue(it)
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}