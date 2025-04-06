package com.summer.core.android.sms.processor

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.sms.data.source.ISmsContentProvider
import com.summer.core.android.sms.data.mapper.SmsMapper
import com.summer.core.domain.model.FetchResult
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.data.local.entities.SmsEntity
import com.summer.core.ml.model.SmsClassifierModel
import com.summer.core.util.CountryCodeProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processes SMS messages in batches, classifies them using ML, and stores them in Room DB.
 * Optimized for performance and resource management.
 *
 * @property smsContentProvider Provides access to the SMS content provider.
 * @property smsDao Data Access Object for SMS storage.
 * @property smsClassifierModel Machine learning model for SMS classification.
 * @property batchProcessingConcurrency Number of concurrent batches to process.
 */
@Singleton
class SmsBatchProcessor @Inject constructor(
    private val smsContentProvider: ISmsContentProvider,
    private val smsDao: SmsDao,
    private val smsClassifierModel: SmsClassifierModel,
    private val countryCodeProvider: CountryCodeProvider,
    private val batchProcessingConcurrency: Int = 2
) {

    private val tag = "SmsBatchProcessor"

    fun processSmsInBatches(batchSize: Int): Flow<FetchResult> = flow {
        try {
            val lastSms = smsDao.getLastInsertedSmsMessage()
            val lastFetchedDate = lastSms?.date
            val lastFetchedId = lastSms?.androidSmsId

            val totalCount = smsContentProvider.getTotalSmsCount().coerceAtLeast(0)
            val processedCount = smsDao.getTotalProcessedSmsCount().coerceAtLeast(0)
            val validBatchSize = batchSize.coerceAtLeast(1)
            val totalBatches = ((totalCount + validBatchSize - 1) / validBatchSize).coerceAtLeast(1)

            var batchNumber = ((processedCount + validBatchSize - 1) / validBatchSize) + 1
            var offset = processedCount
            var hasMoreData = true

            while (hasMoreData) {
                emit(FetchResult.Loading(batchNumber, totalBatches))
                val groupStartTime = System.currentTimeMillis()

                val classifiedResults = processMultipleBatchesAsync(
                    batchSize,
                    offset,
                    lastFetchedDate,
                    lastFetchedId,
                    batchProcessingConcurrency
                )

                val nonEmptyBatches = classifiedResults.filter { it.isNotEmpty() }

                if (nonEmptyBatches.isEmpty()) {
                    hasMoreData = false
                } else {
                    val totalFetched = nonEmptyBatches.sumOf { it.size }
                    insertClassifiedSms(nonEmptyBatches.flatten())
                    offset += totalFetched
                    batchNumber += nonEmptyBatches.size

                    Log.d(
                        tag,
                        "${nonEmptyBatches.size} batches async processed in ${System.currentTimeMillis() - groupStartTime} ms"
                    )

                    // Stop if we processed fewer batches than requested, likely the last ones
                    if (nonEmptyBatches.size < batchProcessingConcurrency) {
                        hasMoreData = false
                    }
                }
            }

            emit(FetchResult.Success)
        } catch (e: Exception) {
            Log.e(tag, "Error processing SMS batches", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            emit(FetchResult.Error(e))
        }
    }.flowOn(Dispatchers.IO) // Use IO dispatcher for potentially blocking operations

    private suspend fun processMultipleBatchesAsync(
        batchSize: Int,
        baseOffset: Int,
        lastFetchedDate: Long?,
        lastFetchedId: Int?,
        concurrency: Int
    ): List<List<SmsEntity>> = coroutineScope {
        (0 until concurrency).map { index ->
            async(Dispatchers.Default) { // Use Default for CPU-bound tasks
                processBatchAsync(
                    batchSize,
                    baseOffset + index * batchSize,
                    lastFetchedDate,
                    lastFetchedId
                )
            }
        }.awaitAll()
    }

    private suspend fun processBatchAsync(
        batchSize: Int,
        offset: Int,
        lastFetchedDate: Long?,
        lastFetchedId: Int?
    ): List<SmsEntity> {
        val cursor =
            smsContentProvider.getSmsCursor(batchSize, offset, lastFetchedDate, lastFetchedId)
        return cursor?.use {
            SmsMapper.mapCursorToSmsList(
                it,
                smsDao,
                countryCodeProvider.getMyCountryCode()
            ).let { smsBatch ->
                if (smsBatch.isNotEmpty()) {
                    classifySmsBatch(smsBatch)
                } else {
                    emptyList()
                }
            }
        } ?: emptyList() // Handle case where cursor is null
    }

    private suspend fun classifySmsBatch(smsBatch: List<SmsEntity>): List<SmsEntity> {
        return smsBatch.map { sms ->
            try {
                val classification = withContext(Dispatchers.Default) { // Run classification on Default
                    smsClassifierModel.classifySms(sms.rawAddress, sms.body)
                }
                sms.copy(
                    importanceScore = classification.importanceScore,
                    smsClassificationTypeId = classification.smsClassificationTypeId,
                    confidenceScore = classification.confidenceScore
                )
            } catch (e: Exception) {
                Log.w(tag, "Error classifying SMS with address: ${sms.rawAddress}, body: ${sms.body}", e)
                FirebaseCrashlytics.getInstance().recordException(e)
                sms // Return original SMS on classification failure
            }
        }
    }

    private suspend fun insertClassifiedSms(smsList: List<SmsEntity>) {
        try {
            smsDao.insertAllSmsMessages(smsList)
        } catch (e: Exception) {
            Log.e(tag, "Error inserting classified SMS messages", e)
            FirebaseCrashlytics.getInstance().recordException(e)
            // Consider how to handle database insertion failures (e.g., retry, report)
        }
    }
}