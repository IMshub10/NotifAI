package com.summer.notifai.ui.datamodel.mapper

import com.summer.core.android.sms.util.SmsMessageType
import com.summer.core.data.local.model.SearchSmsMessageQueryModel
import com.summer.core.ui.model.SmsClassificationType
import com.summer.core.util.DateUtils
import com.summer.notifai.ui.datamodel.SearchSmsMessageDataModel
import com.summer.notifai.ui.datamodel.SmsClassificationDataModel

object SearchSmsMessageMapper {
    fun SearchSmsMessageQueryModel.toSearchSmsMessageDataModel(): SearchSmsMessageDataModel {
        return SearchSmsMessageDataModel(
            id = id,
            senderAddressId = senderAddressId,
            senderAddress = senderAddress,
            message = body.orEmpty(),
            dateInEpoch = date,
            date = DateUtils.formatTimeOnly(date),
            isIncoming = SmsMessageType.isIncoming(type),
            smsClassificationDataModel = SmsClassificationDataModel(
                smsType = smsMessageType.orEmpty(),
                type = SmsClassificationType.fromCompactName(compactType.orEmpty()) ?: SmsClassificationType.IMPORTANT
            )
        )
    }
}