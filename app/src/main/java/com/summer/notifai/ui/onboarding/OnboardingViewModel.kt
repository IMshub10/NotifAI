package com.summer.notifai.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.core.repository.IOnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val onboardingRepository: IOnboardingRepository) :
    ViewModel() {

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

}