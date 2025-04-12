package com.summer.core.domain.usecase

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface DeliveredSmsReceiverEntryPoint {
    fun markSmsAsDelivered(): MarkSmsAsDeliveredUseCase
}