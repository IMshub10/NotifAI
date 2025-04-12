package com.summer.core.domain.model

import java.io.Serializable

sealed class FetchResult : Serializable {
    data class Loading(val processedCount: Int, val totalCount: Int) : FetchResult()
    data object Success : FetchResult() {
        private fun readResolve(): Any = Success
    }

    data class Error(val exception: Exception) : FetchResult()
}