package com.summer.notifai.ui.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.summer.core.repository.IOnboardingRepository
import com.summer.core.android.sms.service.SmsProcessingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val onboardingRepository: IOnboardingRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<StartUiState>()
    val uiState: LiveData<StartUiState> get() = _uiState

    init {
        checkUserAgreement()
        checkSMSProcessingStatus()
    }

    /**
     * Checks if the user has agreed to the user agreement.
     */
    private fun checkUserAgreement() {
        viewModelScope.launch {
            val hasAgreed = onboardingRepository.hasAgreedToUserAgreement()
            updateState(hasAgreedToUserAgreement = hasAgreed)
        }
    }

    /**
     * Checks if SMS processing has been completed.
     */
    private fun checkSMSProcessingStatus() {
        viewModelScope.launch {
            val isProcessingComplete = onboardingRepository.isSMSProcessingCompleted()
            updateState(isSMSProcessingCompleted = isProcessingComplete)
        }
    }

    /**
     * Updates the UI state.
     */
    private fun updateState(
        hasAgreedToUserAgreement: Boolean? = null,
        isSMSProcessingCompleted: Boolean? = null
    ) {
        val currentState = _uiState.value ?: StartUiState()
        _uiState.value = currentState.copy(
            hasAgreedToUserAgreement = hasAgreedToUserAgreement ?: currentState.hasAgreedToUserAgreement,
            isSMSProcessingCompleted = isSMSProcessingCompleted ?: currentState.isSMSProcessingCompleted
        )
    }
}

/**
 * UI state for StartViewModel.
 */
data class StartUiState(
    val hasAgreedToUserAgreement: Boolean? = null,
    val isSMSProcessingCompleted: Boolean? = null
)