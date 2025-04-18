package com.summer.notifai.ui.datamodel

data class SmsMessageDataModel(
    val id: Long,
    val message: String,
    val dateInEpoch: Long,
    val date: String,
    val isIncoming: Boolean,
    val smsClassificationDataModel: SmsClassificationDataModel
){
    override fun equals(other: Any?): Boolean {
        return if (other is SmsMessageDataModel){
            other.id == id
        }else{
            false
        }

    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}