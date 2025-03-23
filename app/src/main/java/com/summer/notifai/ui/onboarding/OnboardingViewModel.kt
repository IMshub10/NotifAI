package com.summer.notifai.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.core.repository.IOnboardingRepository
import com.summer.core.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val onboardingRepository: IOnboardingRepository) :
    ViewModel() {

    fun setUserAgreement(agreed: Boolean) {
        viewModelScope.launch {
            onboardingRepository.setUserAgreement(agreed)
        }
    }

}