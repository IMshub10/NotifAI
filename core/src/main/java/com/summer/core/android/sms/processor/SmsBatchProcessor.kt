package com.summer.core.android.sms.processor

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.summer.core.android.sms.data.ISmsContentProvider
import com.summer.core.android.sms.mapper.SMSMapper
import com.summer.core.data.domain.model.FetchResult
import com.summer.core.data.local.dao.SmsDao
import com.summer.core.ml.model.SmsClassifierModel
import com.summer.core.util.CountryCodeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Processes SMS messages in batches, classifies them using ML, and stores them in Room DB.
 *
 * @property smsContentProvider Provides access to the SMS content provider.
 * @property smsDao Data Access Object for SMS storage.
 * @property smsClassifierModel Machine learning model for SMS classification.
 */
@Singleton
class SmsBatchProcessor @Inject constructor(
    private val smsContentProvider: ISmsContentProvider,
    private val smsDao: SmsDao,
    private val smsClassifierModel: SmsClassifierModel,
    private val countryCodeProvider: CountryCodeProvider
) {

    /**
     * Fetches SMS messages in batches, classifies them, and inserts them into the database.
     * This function emits progress updates and handles resumption from the last processed message.
     *
     * @param batchSize Number of SMS messages to process per batch.
     * @return A Flow emitting [FetchResult] to track progress and completion.
     */
    fun processSmsInBatches(batchSize: Int): Flow<FetchResult> = flow {
        try {
            // Retrieve the last processed SMS from the database
            val lastSms = smsDao.getLastInsertedSmsMessage()
            val lastFetchedDate = lastSms?.date
            val lastFetchedId = lastSms?.androidSmsId

            // Get the total SMS count and already processed messages count
            val totalCount =
                smsContentProvider.getTotalSmsCount().coerceAtLeast(0) // Ensure non-negative
            val processedCount =
                smsDao.getTotalProcessedSmsCount().coerceAtLeast(0) // Ensure non-negative

            // Avoid division by zero or negative values
            val validBatchSize = batchSize.coerceAtLeast(1)

            // Calculate the total number of batches safely
            val totalBatches = ((totalCount + validBatchSize - 1) / validBatchSize).coerceAtLeast(1)
            var batchNumber =
                ((processedCount + validBatchSize - 1) / validBatchSize) + 1 // Ensure valid numbering
            var offset = processedCount // Resume from last processed SMS count
            var hasMoreData = true

            // Process SMS messages in batches
            while (hasMoreData) {
                val start = System.currentTimeMillis()
                emit(FetchResult.Loading(batchNumber, totalBatches)) // Emit progress

                // Fetch SMS batch from content provider
                val cursor = smsContentProvider.getSmsCursor(
                    batchSize,
                    offset,
                    lastFetchedDate,
                    lastFetchedId
                )
                val smsBatch = SMSMapper.mapCursorToSmsList(
                    cursor,
                    smsDao,
                    countryCodeProvider.getMyCountryCode()
                )

                // Check if the batch is empty, marking the end of processing
                if (smsBatch.isEmpty()) {
                    hasMoreData = false
                } else {
                    // Run ML classification on each SMS message
                    val classifiedSms = smsBatch.map { sms ->
                        try {
                            val classification =
                                smsClassifierModel.classifySms(sms.rawAddress, sms.body)
                            sms.copy(
                                importanceScore = classification.importanceScore,
                                smsClassificationTypeId = classification.smsClassificationTypeId,
                                confidenceScore = classification.confidenceScore
                            )
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(e)
                            e.printStackTrace()
                            sms
                        }
                    }

                    // Insert classified messages into the Room database
                    smsDao.insertAllSmsMessages(classifiedSms)

                    // Update offset dynamically based on actual batch size
                    offset += smsBatch.size
                    batchNumber++
                    Log.d(
                        "ProcessingTime",
                        "Batch processed in ${System.currentTimeMillis() - start} ms"
                    )
                }
            }

            emit(FetchResult.Success) // Emit final success after all batches are processed

        } catch (e: Exception) {
            emit(FetchResult.Error(e)) // Emit error in case of failure
        }
    }.flowOn(Dispatchers.Default) // Ensure execution happens on IO thread for performance
}