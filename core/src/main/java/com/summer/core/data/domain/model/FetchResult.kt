package com.summer.core.data.domain.model

import java.io.Serializable

sealed class FetchResult : Serializable{
    data class Loading(val batchNumber: Int, val totalBatches: Int) : FetchResult()
    data object Success : FetchResult() {
        private fun readResolve(): Any = Success
    }
    data class Error(val exception: Exception) : FetchResult()
}