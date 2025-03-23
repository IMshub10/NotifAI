package com.summer.notifai.ui.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.core.repository.IOnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val onboardingRepository: IOnboardingRepository
) : ViewModel() {

    private val _hasAgreedToUserAgreement = MutableLiveData<Boolean?>()
    val hasAgreedToUserAgreement: LiveData<Boolean?> get() = _hasAgreedToUserAgreement

    init {
        checkUserAgreement()
    }

    private fun checkUserAgreement() {
        viewModelScope.launch {
            _hasAgreedToUserAgreement.value = onboardingRepository.hasAgreedToUserAgreement()
        }
    }

}