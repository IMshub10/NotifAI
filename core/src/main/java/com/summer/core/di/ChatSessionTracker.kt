package com.summer.core.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSessionTracker @Inject constructor() {
    @Volatile
    var activeSenderAddressId: Long? = null
}