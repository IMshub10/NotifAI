package com.summer.core.di

import com.summer.core.domain.repository.IContactRepository
import com.summer.core.domain.usecase.SyncContactsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContactObserverEntryPoint {
    fun contactRepository(): IContactRepository
    fun syncContactsUseCase(): SyncContactsUseCase
}