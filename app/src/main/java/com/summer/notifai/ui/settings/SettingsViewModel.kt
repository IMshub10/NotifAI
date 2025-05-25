package com.summer.notifai.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.summer.core.data.local.preference.PreferenceKey
import com.summer.core.data.local.preference.SharedPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _storeInSystemSms = MutableLiveData<Boolean>()
    val storeInSystemSms: LiveData<Boolean> get() = _storeInSystemSms

    fun loadStoreInSystemSmsPreference() {
        val saveDataInPublicDb = sharedPreferencesManager.getDataBoolean(
            PreferenceKey.SAVE_DATA_IN_PUBLIC_DB,
            true
        )
        _storeInSystemSms.value = saveDataInPublicDb
    }

    fun updateStoreInSystemSms(enabled: Boolean) {
        sharedPreferencesManager.saveData(
            PreferenceKey.SAVE_DATA_IN_PUBLIC_DB,
            enabled
        )
        _storeInSystemSms.value = enabled
    }
}
