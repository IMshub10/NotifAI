package com.summer.notifai.service

import android.service.carrier.CarrierMessagingService
import android.service.carrier.MessagePdu

class MyCarrierMessagingService : CarrierMessagingService() {
    override fun onReceiveTextSms(
        pdu: MessagePdu,
        format: String,
        destPort: Int,
        subId: Int,
        callback: ResultCallback<Int>
    ) {
        callback.onReceiveResult(0)
    }
}