package com.summer.core.data.local.preference

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SharedPreferencesManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

    fun saveData(key: PreferenceKeys, data: String) {
        sharedPreferences.edit { putString(key.key, data) }
    }

    fun getData(key: PreferenceKeys, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key.key, defaultValue)
    }

    fun saveData(key: PreferenceKeys, data: Int) {
        sharedPreferences.edit { putInt(key.key, data) }
    }

    fun getDataInt(key: PreferenceKeys, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key.key, defaultValue)
    }

    fun saveData(key: PreferenceKeys, data: Boolean) {
        sharedPreferences.edit { putBoolean(key.key, data) }
    }

    fun getDataBoolean(key: PreferenceKeys, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.key, defaultValue)
    }

    fun saveData(key: PreferenceKeys, data: Float) {
        sharedPreferences.edit { putFloat(key.key, data) }
    }

    fun getDataFloat(key: PreferenceKeys, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key.key, defaultValue)
    }

    fun saveData(key: PreferenceKeys, data: Long) {
        sharedPreferences.edit { putLong(key.key, data) }
    }

    fun getDataLong(key: PreferenceKeys, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key.key, defaultValue)
    }

    fun saveData(key: PreferenceKeys, data: Set<String>) {
        sharedPreferences.edit { putStringSet(key.key, data) }
    }

    fun getDataStringSet(key: PreferenceKeys, defaultValue: Set<String>? = null): Set<String>? {
        return sharedPreferences.getStringSet(key.key, defaultValue)
    }
}