package com.summer.core.util

sealed class ResultState<out T> {
    data object InProgress : ResultState<Nothing>()
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Failed(val error: Throwable) : ResultState<Nothing>()
}