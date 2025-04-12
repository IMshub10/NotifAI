package com.summer.core.di

import com.summer.core.domain.usecase.MarkSmsAsSentUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SentSmsReceiverEntryPoint {
    fun markSmsAsSent(): MarkSmsAsSentUseCase
}