package com.summer.core.di

import com.summer.core.repository.IContactRepository
import com.summer.core.usecase.SyncContactsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ContactObserverEntryPoint {
    fun contactRepository(): IContactRepository
    fun syncContactsUseCase(): SyncContactsUseCase
}